package cn.javaer.snippets.jooq.condition;

import cn.javaer.snippets.jooq.Sql;
import cn.javaer.snippets.jooq.condition.annotation.ConditionBetweenMax;
import cn.javaer.snippets.jooq.condition.annotation.ConditionBetweenMin;
import cn.javaer.snippets.jooq.condition.annotation.ConditionContained;
import cn.javaer.snippets.jooq.condition.annotation.ConditionContains;
import cn.javaer.snippets.jooq.condition.annotation.ConditionEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionGreaterOrEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionGreaterThan;
import cn.javaer.snippets.jooq.condition.annotation.ConditionIgnore;
import cn.javaer.snippets.jooq.condition.annotation.ConditionLessOrEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionLessThan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Condition 条件创建器，根据 POJO 来动态创建条件.
 *
 * @author cn-src
 */
public class ConditionCreator {
    private static final ConcurrentHashMap<Class<?>, List<ClassInfo>> cache = new ConcurrentHashMap<>();
    private static final Map<Class<? extends Annotation>, BiFunction<Field, Object, Condition>> conditionFunMap = new HashMap<>();

    static {
        //noinspection unchecked
        conditionFunMap.put(ConditionEqual.class, Field::eq);
        //noinspection unchecked
        conditionFunMap.put(ConditionLessThan.class, Field::lessThan);
        //noinspection unchecked
        conditionFunMap.put(ConditionLessOrEqual.class, Field::lessOrEqual);
        //noinspection unchecked
        conditionFunMap.put(ConditionGreaterThan.class, Field::greaterThan);
        //noinspection unchecked
        conditionFunMap.put(ConditionGreaterOrEqual.class, Field::greaterOrEqual);
    }

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

                    if (info.conditionFun != null) {
                        conditions.add(info.conditionFun.apply(column, value));
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
            final Annotation[] fieldAnnotations = field.getAnnotations();
            Annotation conditionAnnotation = null;
            if (fieldAnnotations != null) {
                for (final Annotation fieldAnnotation : fieldAnnotations) {
                    if (!AnnotatedElementUtils.isAnnotated(fieldAnnotation.annotationType(), cn.javaer.snippets.jooq.condition.annotation.Condition.class)) {
                        continue;
                    }
                    if (null != conditionAnnotation) {
                        throw new IllegalStateException("Condition annotation has multi");
                    }
                    conditionAnnotation = fieldAnnotation;
                }
            }
            final BiFunction<Field, Object, Condition> conditionFun = conditionAnnotation == null ?
                    null : conditionFunMap.get(conditionAnnotation.annotationType());
            classInfos.add(new ClassInfo(conditionAnnotation,
                    dr.getReadMethod(), DSL.field(underline(name)),
                    conditionFun));
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

        @SuppressWarnings("rawtypes")
        private BiFunction<Field, Object, Condition> conditionFun;
    }
}
