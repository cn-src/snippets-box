package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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
    private final List<Condition> conditions = new ArrayList<>();

    public ConditionBuilder append(final Supplier<Condition> supplier) {
        this.conditions.add(supplier.get());
        return this;
    }

    public <T> ConditionBuilder append(final Function<T, Condition> fun, final T value) {
        if (ObjectUtils.isEmpty(value)) {
            return this;
        }

        this.conditions.add(fun.apply(value));
        return this;
    }

    @SafeVarargs
    public final <T> ConditionBuilder append(final Function<T[], Condition> fun, final T... values) {
        if (ObjectUtils.isEmpty(values)) {
            return this;
        }

        //noinspection unchecked
        final T[] objects = (T[]) Arrays.stream(values)
                .filter(Objects::nonNull)
                .toArray();

        if (objects.length == 0) {
            return this;
        }

        this.conditions.add(fun.apply(objects));
        return this;
    }

    public ConditionBuilder append(final Function<String[], Condition> fun, final String... values) {
        if (ObjectUtils.isEmpty(values)) {
            return this;
        }

        final String[] toArray = Arrays.stream(values)
                .filter(StringUtils::hasLength)
                .toArray(String[]::new);

        if (toArray.length == 0) {
            return this;
        }

        this.conditions.add(fun.apply(toArray));
        return this;
    }

    public ConditionBuilder append(final BiFunction<LocalDateTime, LocalDateTime, Condition> fun, final LocalDate start, final LocalDate end) {
        if (null == start || null == end) {
            return this;
        }
        final LocalDateTime startTime = start.atStartOfDay();
        final LocalDateTime endTime = end.atTime(LocalTime.MAX);
        this.conditions.add(fun.apply(startTime, endTime));
        return this;
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
