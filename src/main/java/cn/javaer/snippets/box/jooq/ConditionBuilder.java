package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author cn-src
 */
public class ConditionBuilder {
    // TODO
    private final boolean ignoreNull = true;
    private final boolean ignoreEmpty = true;
    private final boolean ignoreBlank = true;
    private final List<Condition> conditions = new ArrayList<>();

    public ConditionBuilder append(final Supplier<Condition> supplier) {
        this.conditions.add(supplier.get());
        return this;
    }

    public <T> ConditionBuilder append(final Function<T, Condition> fun, final T value) {
        if (this.ignoreNull && null == value) {
            return this;
        }
        this.conditions.add(fun.apply(value));
        return this;
    }

    public ConditionBuilder append(final Function<String, Condition> fun, final String value) {
        if (this.ignoreNull && null == value) {
            return this;
        }
        if (this.ignoreEmpty && (null == value || value.isEmpty())) {
            return this;
        }
        if (this.ignoreBlank && (null == value || value.isEmpty() || value.trim().isEmpty())) {
            return this;
        }
        this.conditions.add(fun.apply(value));
        return this;
    }

    public ConditionBuilder append(final BiFunction<LocalDateTime, LocalDateTime, Condition> fun, final LocalDate start, final LocalDate end) {
        if (this.ignoreNull && null == start && null == end) {
            return this;
        }
        final LocalDateTime startTime = Objects.requireNonNull(start).atStartOfDay();
        final LocalDateTime endTime = end.atTime(LocalTime.MAX);
        this.conditions.add(fun.apply(startTime, endTime));
        return this;
    }

    @Nullable
    public Condition build() {
        if (this.conditions.isEmpty()) {
            return null;
        }
        final Condition condition = this.conditions.get(0);
        for (int i = 1, size = this.conditions.size(); i < size; i++) {
            condition.and(this.conditions.get(i));
        }
        return condition;
    }
}
