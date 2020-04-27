package cn.javaer.snippets.box.jooq;

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
import java.util.List;

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

        if (conditions.isEmpty()) {
            return null;
        }
        Condition condition = conditions.get(0);
        for (int i = 1, size = conditions.size(); i < size; i++) {
            condition = condition.and(conditions.get(i));
        }
        return condition;
    }
}
