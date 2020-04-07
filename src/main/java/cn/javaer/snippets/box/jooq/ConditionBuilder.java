package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author cn-src
 */
public class ConditionBuilder {

    @FunctionalInterface
    public interface Function3<T1, T2, T3>
            extends Serializable {
        /**
         * The three parameters Function.
         *
         * @param t1 t1
         * @param t2 t2
         * @param t3 t3
         *
         * @return Condition
         */
        Condition apply(T1 t1, T2 t2, T3 t3);
    }

    private final List<Condition> conditions = new ArrayList<>();

    public ConditionBuilder append(final Supplier<Condition> supplier) {
        this.conditions.add(supplier.get());
        return this;
    }

    public <T> ConditionBuilder append(final Function<T, Condition> fun, final T value) {
        final T obj = this.filter(value);
        if (ObjectUtils.isEmpty(obj)) {
            return this;
        }

        this.conditions.add(fun.apply(obj));
        return this;
    }

    public <T1, T2> ConditionBuilder append(final BiFunction<T1, T2, Condition> fun, final T1 t1, final T2 t2) {
        final T1 obj1 = this.filter(t1);
        final T2 obj2 = this.filter(t2);
        if (ObjectUtils.isEmpty(obj1) || ObjectUtils.isEmpty(obj2)) {
            return this;
        }
        this.conditions.add(fun.apply(obj1, obj2));
        return this;
    }

    public ConditionBuilder dateTime(final BiFunction<LocalDateTime, LocalDateTime, Condition> fun, final LocalDate start, final LocalDate end) {
        if (null == start || null == end) {
            return this;
        }
        final LocalDateTime startTime = start.atStartOfDay();
        final LocalDateTime endTime = end.atTime(LocalTime.MAX);
        this.conditions.add(fun.apply(startTime, endTime));
        return this;
    }

    public <T1, T2, T3> ConditionBuilder append(final Function3<T1, T2, T3> fun, final T1 t1, final T2 t2, final T3 t3) {
        final T1 obj1 = this.filter(t1);
        final T2 obj2 = this.filter(t2);
        final T3 obj3 = this.filter(t3);
        if (ObjectUtils.isEmpty(obj1) || ObjectUtils.isEmpty(obj2) || ObjectUtils.isEmpty(obj3)) {
            return this;
        }
        this.conditions.add(fun.apply(obj1, obj2, obj3));
        return this;
    }

    @SuppressWarnings("unchecked")
    private <T> T filter(final T t) {
        if (null == t) {
            return null;
        }
        if (t instanceof String[]) {
            return (T) Arrays.stream((String[]) t)
                    .filter(StringUtils::hasLength)
                    .toArray(String[]::new);
        }
        if (t instanceof Object[]) {
            return (T) Arrays.stream((Object[]) t)
                    .filter(Objects::nonNull)
                    .toArray();
        }
        return t;
    }

    @Nullable
    public Condition build() {
        if (this.conditions.isEmpty()) {
            return null;
        }
        Condition condition = this.conditions.get(0);
        for (int i = 1, size = this.conditions.size(); i < size; i++) {
            condition = condition.and(this.conditions.get(i));
        }
        return condition;
    }
}
