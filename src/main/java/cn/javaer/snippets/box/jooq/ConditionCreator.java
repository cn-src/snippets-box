package cn.javaer.snippets.box.jooq;

import lombok.Data;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Condition 条件创建器，根据 POJO 来动态创建条件.
 *
 * @author cn-src
 */
public class ConditionCreator {

    public static Condition create(final Object query) {
        return ConditionCreator.create(query, false);
    }

    public static Condition createWithIgnoreUnannotated(final Object query) {
        return ConditionCreator.create(query, true);
    }

    private static Condition create(final Object query, final boolean ignoreUnannotated) {
        if (query == null) {
            return null;
        }

        final List<Condition> conditions = new ArrayList<>();
        final Class<?> clazz = query.getClass();
        final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        final Map<String, BetweenValue> betweenValueMap = new HashMap<>();
        try {
            for (final PropertyDescriptor dr : descriptors) {
                final String name = dr.getName();
                final java.lang.reflect.Field field = ReflectionUtils.findField(clazz, name);
                if ("class".equals(name) || field == null || field.getAnnotation(ConditionIgnore.class) != null) {
                    continue;
                }
                final Object value = dr.getReadMethod().invoke(query);
                if (ObjectUtils.isEmpty(value)) {
                    continue;
                }
                //noinspection rawtypes
                final Field jooqField = DSL.field(name);
                if (field.getAnnotation(ConditionContains.class) != null) {
                    if (dr.getPropertyType().equals(JSONB.class)) {
                        @SuppressWarnings("rawtypes") final Field jsonField = jooqField;
                        //noinspection unchecked
                        conditions.add(Sql.jsonbContains(jsonField, (JSONB) value));
                    }
                    else {
                        //noinspection unchecked
                        conditions.add(jooqField.contains(value));
                    }
                }
                else if (field.getAnnotation(ConditionContained.class) != null && dr.getPropertyType().equals(String[].class)) {
                    //noinspection unchecked
                    conditions.add(Sql.arrayContained(jooqField, (String[]) value));
                }
                else if (field.getAnnotation(ConditionBetweenMin.class) != null) {
                    final ConditionBetweenMin betweenMin = field.getAnnotation(ConditionBetweenMin.class);
                    BetweenValue betweenValue = betweenValueMap.get(betweenMin.value());
                    if (null == betweenValue) {
                        betweenValue = new BetweenValue();
                        betweenValueMap.put(betweenMin.value(), betweenValue);
                    }
                    betweenValue.setMin(value);
                }
                else if (field.getAnnotation(ConditionBetweenMax.class) != null) {
                    final ConditionBetweenMax betweenMax = field.getAnnotation(ConditionBetweenMax.class);
                    BetweenValue betweenValue = betweenValueMap.get(betweenMax.value());
                    if (null == betweenValue) {
                        betweenValue = new BetweenValue();
                        betweenValueMap.put(betweenMax.value(), betweenValue);
                    }
                    betweenValue.setMax(value);
                }
                else {
                    if (!ignoreUnannotated) {
                        //noinspection unchecked
                        conditions.add(jooqField.eq(value));
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
    static class BetweenValue {
        Object min;
        Object max;
    }
}
