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
 * @author cn-src
 */
public class ConditionCreator {

    public static Condition create(final Object query) {
        final List<Condition> conditions = new ArrayList<>();
        final Class<?> clazz = query.getClass();
        final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(clazz);
        try {
            for (final PropertyDescriptor dr : descriptors) {
                final String name = dr.getName();
                if ("class".equals(name)) {
                    continue;
                }
                final Object value = dr.getReadMethod().invoke(query);
                if (ObjectUtils.isEmpty(value)) {
                    continue;
                }
                final java.lang.reflect.Field field = ReflectionUtils.findField(clazz, name);
                if (null != field && field.getAnnotation(Contains.class) != null) {
                    if (dr.getPropertyType().equals(JSONB.class)) {
                        @SuppressWarnings("rawtypes") final Field jsonField = DSL.field(name);
                        //noinspection unchecked
                        conditions.add(Sql.jsonbContains(jsonField, (JSONB) value));
                    }
                    else {
                        conditions.add(DSL.field(name).contains(value));
                    }
                }
                else {
                    conditions.add(DSL.field(name).eq(value));
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
