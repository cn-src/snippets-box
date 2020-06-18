package cn.javaer.snippets.box.jooq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Condition 条件创建器，根据 POJO 来动态创建条件.
 *
 * @author cn-src
 */
public class ConditionCreator {
    private static final ConcurrentHashMap<Class<?>, List<ClassInfo>> cache = new ConcurrentHashMap<>();

    public static Condition create(final Object query) {
        return ConditionCreator.create(query, false);
    }

    public static Condition createWithIgnoreUnannotated(final Object query) {
        return ConditionCreator.create(query, true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Condition create(final Object query, final boolean ignoreUnannotated) {
        if (query == null) {
            return null;
        }

        final List<Condition> conditions = new ArrayList<>();
        final Class<?> clazz = query.getClass();
        final List<ClassInfo> classInfos = cache.computeIfAbsent(clazz, ConditionCreator::createClassCache);
        final Map<String, BetweenValue> betweenValueMap = new HashMap<>();
        try {
            for (final ClassInfo info : classInfos) {
                final Object value = info.readMethod.invoke(query);
                if (ObjectUtils.isEmpty(value)) {
                    continue;
                }
                final Annotation ann = info.annotation;
                final Field column = info.column;
                if (ann == null) {
                    if (!ignoreUnannotated) {
                        conditions.add(column.eq(value));
                    }
                }
                else {
                    if (ann instanceof ConditionEqual) {
                        conditions.add(column.eq(value));
                    }
                    else if (ann instanceof ConditionContains) {
                        if (JSONB.class.equals(info.readMethod.getReturnType())) {
                            conditions.add(Sql.jsonbContains(column, (JSONB) value));
                        }
                        else {
                            conditions.add(column.contains(value));
                        }
                    }
                    else if (ann instanceof ConditionContained
                            && String[].class.equals(info.readMethod.getReturnType())) {
                        conditions.add(Sql.arrayContained(column, (String[]) value));
                    }
                    else if (ann instanceof ConditionLessThan) {
                        conditions.add(column.lessThan(value));
                    }
                    else if (ann instanceof ConditionGreaterThan) {
                        conditions.add(column.greaterThan(value));
                    }
                    if (ann instanceof ConditionLessOrEqual) {
                        conditions.add(column.lessOrEqual(value));
                    }
                    else if (ann instanceof ConditionGreaterOrEqual) {
                        conditions.add(column.greaterOrEqual(value));
                    }
                    else if (ann instanceof ConditionBetweenMin) {
                        final ConditionBetweenMin betweenMin = (ConditionBetweenMin) ann;
                        final String betweenColumn = betweenMin.value().isEmpty() ? betweenMin.column() : betweenMin.value();
                        Assert.hasText(betweenColumn, () -> "Column must be not empty");
                        BetweenValue betweenValue = betweenValueMap.get(betweenColumn);
                        if (null == betweenValue) {
                            betweenValue = new BetweenValue();
                            betweenValueMap.put(betweenColumn, betweenValue);
                        }
                        if (betweenMin.dateToDateTime() && LocalDate.class.equals(info.readMethod.getReturnType())) {
                            betweenValue.setMin(((LocalDate) value).atTime(LocalTime.MIN));
                        }
                        else {
                            betweenValue.setMin(value);
                        }
                    }
                    else if (ann instanceof ConditionBetweenMax) {
                        final ConditionBetweenMax betweenMax = (ConditionBetweenMax) ann;
                        final String betweenColumn = betweenMax.value().isEmpty() ? betweenMax.column() : betweenMax.value();
                        Assert.hasText(betweenColumn, () -> "Column must be not empty");
                        BetweenValue betweenValue = betweenValueMap.get(betweenColumn);
                        if (null == betweenValue) {
                            betweenValue = new BetweenValue();
                            betweenValueMap.put(betweenColumn, betweenValue);
                        }
                        if (betweenMax.dateToDateTime() && LocalDate.class.equals(info.readMethod.getReturnType())) {
                            betweenValue.setMax(((LocalDate) value).atTime(LocalTime.MAX));
                        }
                        else {
                            betweenValue.setMax(value);
                        }
                    }
                }
            }
        }
        catch (final IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }

        for (final Map.Entry<String, BetweenValue> entry : betweenValueMap.entrySet()) {
            final BetweenValue value = entry.getValue();
            conditions.add(DSL.field(underline(entry.getKey())).between(value.getMin(), value.getMax()));
        }

        if (conditions.isEmpty()) {
            return null;
        }
        Condition condition = conditions.get(0);
        for (int i = 1, size = conditions.size(); i < size; i++) {
            condition = condition.and(conditions.get(i));
        }

        return condition;
    }

    private static List<ClassInfo> createClassCache(final Class<?> clazz) {
        final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        final List<ClassInfo> classInfos = new ArrayList<>(descriptors.length);
        for (final PropertyDescriptor dr : descriptors) {
            final String name = dr.getName();
            final java.lang.reflect.Field field = ReflectionUtils.findField(clazz, name);
            if ("class".equals(name) || field == null || field.getAnnotation(ConditionIgnore.class) != null) {
                continue;
            }
            final ConditionContains conditionContains = field.getAnnotation(ConditionContains.class);
            final ConditionContained conditionContained = field.getAnnotation(ConditionContained.class);
            final ConditionBetweenMin conditionBetweenMin = field.getAnnotation(ConditionBetweenMin.class);
            final ConditionBetweenMax conditionBetweenMax = field.getAnnotation(ConditionBetweenMax.class);
            final ConditionEqual conditionEqual = field.getAnnotation(ConditionEqual.class);
            final Annotation[] annotations = Stream.of(conditionContains, conditionContained,
                    conditionBetweenMin, conditionBetweenMax, conditionEqual)
                    .filter(Objects::nonNull)
                    .toArray(Annotation[]::new);
            Assert.state(annotations.length < 2, () -> "Condition annotation has multi");
            classInfos.add(new ClassInfo(annotations.length == 1 ? annotations[0] : null, dr.getReadMethod(), DSL.field(underline(name))));
        }
        return classInfos;
    }

    private static String underline(final String str) {
        final char[] chars = str.toCharArray();
        final StringBuilder sb = new StringBuilder(chars.length);
        for (final char c : chars) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class BetweenValue {
        Object min;
        Object max;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ClassInfo {
        @Nullable
        private Annotation annotation;
        private Method readMethod;
        @SuppressWarnings("rawtypes")
        private Field column;
    }
}
