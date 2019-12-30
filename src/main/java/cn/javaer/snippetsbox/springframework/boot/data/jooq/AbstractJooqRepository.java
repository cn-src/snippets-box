package cn.javaer.snippetsbox.springframework.boot.data.jooq;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.support.ExampleMatcherAccessor;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;

import java.util.Optional;

/**
 * @author cn-src
 */
public abstract class AbstractJooqRepository<T, ID> {
    private final DSLContext dsl;
    private final RelationalPersistentEntity<?> persistentEntity;
    private final Table<Record> table;

    protected AbstractJooqRepository(final DSLContext dsl, final RelationalPersistentEntity<T> persistentEntity) {
        this.dsl = dsl;
        this.persistentEntity = persistentEntity;
        this.table = DSL.table(this.persistentEntity.getTableName());
    }

    protected ResultQuery<Record> findAllStep(final Sort sort) {
        final SelectWhereStep<Record> step = this.dsl.selectFrom(this.table);
        this.sortStep(step, sort);
        return step;
    }

    protected ResultQuery<Record> findAllStep(final Pageable pageable) {
        final SelectWhereStep<Record> step = this.dsl.selectFrom(this.table);
        this.pageableStep(step, pageable);
        return step;
    }

    protected <S extends T> ResultQuery<Record> findOneStep(final Example<S> example) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        return step;
    }

    protected <S extends T> ResultQuery<Record> findAllStep(final Example<S> example) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        return step;
    }

    protected <S extends T> ResultQuery<Record> findAllStep(final Example<S> example, final Sort sort) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        this.sortStep(step, sort);
        return step;
    }

    protected <S extends T> ResultQuery<Record> findAllStep(final Example<S> example, final Pageable pageable) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        this.pageableStep(step, pageable);
        return step;
    }

    protected <S extends T> ResultQuery<Record1<Integer>> countStep(final Example<S> example) {
        final SelectConditionStep<Record1<Integer>> step = this.dsl.selectCount().from(this.table).where();
        this.exampleStep(step, example);
        return step;
    }

    protected <S extends T> void exampleStep(final SelectConditionStep<? extends Record> step, final Example<S> example) {
        final ExampleMatcher matcher = example.getMatcher();
        final ExampleMatcherAccessor exampleAccessor = new ExampleMatcherAccessor(matcher);
        final DirectFieldAccessFallbackBeanWrapper beanWrapper = new DirectFieldAccessFallbackBeanWrapper(example.getProbe());

        for (final RelationalPersistentProperty persistentProperty : this.persistentEntity) {
            final String propertyName = persistentProperty.getName();
            if (exampleAccessor.isIgnoredPath(propertyName)) {
                continue;
            }
            final ExampleMatcher.PropertyValueTransformer transformer = exampleAccessor.getValueTransformerForPath(propertyName);
            final Optional<Object> optionalValue = transformer
                    .apply(Optional.ofNullable(beanWrapper.getPropertyValue(propertyName)));
            final String columnName = persistentProperty.getColumnName();

            final Condition condition;
            if (optionalValue.isPresent()) {
                condition = DSL.field(columnName).eq(optionalValue.get());
            }
            else if (exampleAccessor.getNullHandler().equals(ExampleMatcher.NullHandler.INCLUDE)) {
                condition = DSL.field(columnName).isNull();
            }
            else {
                continue;
            }

            if (matcher.isAllMatching()) {
                step.and(condition);
            }
            else {
                step.or(condition);
            }
        }
    }

    protected void pageableStep(final SelectOrderByStep<Record> step, final Pageable pageable) {
        this.sortStep(step, pageable.getSort());
        step.offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    protected void sortStep(final SelectOrderByStep<Record> step, final Sort sort) {
        if (sort.isSorted()) {
            //noinspection rawtypes
            final SortField[] fields = sort.map(it -> it.isAscending() ? DSL.field(it.getProperty()).asc()
                    : DSL.field(it.getProperty()).desc()).toList().toArray(new SortField[0]);
            step.orderBy(fields);
        }
    }
}
