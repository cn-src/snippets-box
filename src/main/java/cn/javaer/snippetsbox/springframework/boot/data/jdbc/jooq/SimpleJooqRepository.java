/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

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
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jooq.SelectQueryMapper;
import org.simpleflatmapper.jooq.SelectQueryMapperFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.support.ExampleMatcherAccessor;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link JooqRepository} interface.
 *
 * @author cn-src
 */
@Transactional(readOnly = true)
public class SimpleJooqRepository<T, ID> implements JooqRepository<T, ID> {

    private final DSLContext dsl;
    private final JdbcAggregateOperations entityOperations;
    private final JdbcMapper<T> jdbcMapper;
    private final SelectQueryMapper<T> queryMapper;
    private final RelationalPersistentEntity<?> persistentEntity;
    private final Class<T> type;
    private final Table<Record> table;

    SimpleJooqRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, DSLContext dsl, RelationalMappingContext context) {
        this.dsl = dsl;
        this.entityOperations = entityOperations;
        jdbcMapper = JdbcMapperFactory.newInstance().ignorePropertyNotFound().newMapper(entity.getType());
        queryMapper = SelectQueryMapperFactory.newInstance().ignorePropertyNotFound().newMapper(entity.getType());
        persistentEntity = context.getRequiredPersistentEntity(entity.getType());
        type = entity.getType();
        table = DSL.table(persistentEntity.getTableName());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(S)
     */
    @Transactional
    @Override
    public <S extends T> S save(S instance) {
        return entityOperations.save(instance);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#save(java.lang.Iterable)
     */
    @Transactional
    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {

        return Streamable.of(entities).stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findOne(java.io.Serializable)
     */
    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entityOperations.findById(id, type));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#exists(java.io.Serializable)
     */
    @Override
    public boolean existsById(ID id) {
        return entityOperations.existsById(id, type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll()
     */
    @Override
    public Iterable<T> findAll() {
        return entityOperations.findAll(type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#findAll(java.lang.Iterable)
     */
    @Override
    public Iterable<T> findAllById(Iterable<ID> ids) {
        return entityOperations.findAllById(ids, type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#count()
     */
    @Override
    public long count() {
        return entityOperations.count(type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
     */
    @Transactional
    @Override
    public void deleteById(ID id) {
        entityOperations.deleteById(id, type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Transactional
    @Override
    public void delete(T instance) {
        entityOperations.delete(instance, type);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
     */
    @Transactional
    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(it -> entityOperations.delete(it, (Class<T>) it.getClass()));
    }

    @Transactional
    @Override
    public void deleteAll() {
        entityOperations.deleteAll(type);
    }

    @Override
    public Iterable<T> findAll(Sort sort) {
        SelectWhereStep<Record> step = dsl.selectFrom(table);
        sortStep(step, sort);
        return toList(step);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        SelectWhereStep<Record> step = dsl.selectFrom(table);
        pageableStep(step, pageable);
        return new PageImpl<>(toList(step), pageable, count());
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        SelectConditionStep<Record> step = dsl.selectFrom(table).where();
        exampleStep(step, example);
        return toOne(step);
    }

    @Override
    public <S extends T> Iterable<S> findAll(Example<S> example) {
        SelectConditionStep<Record> step = dsl.selectFrom(table).where();
        exampleStep(step, example);
        return toList(step);
    }

    @Override
    public <S extends T> Iterable<S> findAll(Example<S> example, Sort sort) {
        SelectConditionStep<Record> step = dsl.selectFrom(table).where();
        exampleStep(step, example);
        sortStep(step, sort);
        return toList(step);
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        SelectConditionStep<Record> step = dsl.selectFrom(table).where();
        exampleStep(step, example);
        pageableStep(step, pageable);

        SelectConditionStep<Record1<Integer>> countStep = dsl.selectCount().from(table).where();
        exampleStep(countStep, example);
        int count = dsl.fetchCount(countStep);

        return new PageImpl<>(toList(step), pageable, count);
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        SelectConditionStep<Record1<Integer>> countStep = dsl.selectCount().from(table).where();
        exampleStep(countStep, example);
        return dsl.fetchCount(countStep);
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return count(example) > 0;
    }

    @Override
    public List<T> findAll(QueryStep queryStep) {
        return queryMapper.asList(queryStep.step(dsl));
    }

    @Override
    public Optional<T> findOne(QueryStep queryStep) {
        return Optional.ofNullable(DataAccessUtils.nullableSingleResult(queryMapper.asList(queryStep.step(dsl))));
    }

    private <S extends T> void exampleStep(SelectConditionStep<? extends Record> step, Example<S> example) {
        ExampleMatcher matcher = example.getMatcher();
        ExampleMatcherAccessor exampleAccessor = new ExampleMatcherAccessor(matcher);
        DirectFieldAccessFallbackBeanWrapper beanWrapper = new DirectFieldAccessFallbackBeanWrapper(example.getProbe());

        for (RelationalPersistentProperty persistentProperty : persistentEntity) {
            if (exampleAccessor.isIgnoredPath(persistentProperty.getName())) {
                continue;
            }
            ExampleMatcher.PropertyValueTransformer transformer = exampleAccessor.getValueTransformerForPath(persistentProperty.getName());
            Optional<Object> optionalValue = transformer
                    .apply(Optional.ofNullable(beanWrapper.getPropertyValue(persistentProperty.getName())));
            String columnName = persistentProperty.getColumnName();
            if (!optionalValue.isPresent()) {

                if (exampleAccessor.getNullHandler().equals(ExampleMatcher.NullHandler.INCLUDE)) {
                    if (matcher.isAllMatching()) {
                        step.and(DSL.field(columnName).isNull());
                    }
                    else {
                        step.or(DSL.field(columnName).isNull());
                    }
                }
                continue;
            }

            Object attributeValue = optionalValue.get();
            if (matcher.isAllMatching()) {
                step.and(DSL.field(columnName).eq(attributeValue));
            }
            else {
                step.or(DSL.field(columnName).eq(attributeValue));
            }
        }
    }

    private void pageableStep(SelectOrderByStep<Record> step, Pageable pageable) {
        sortStep(step, pageable.getSort());
        step.offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    private void sortStep(SelectOrderByStep<Record> step, Sort sort) {
        if (sort.isSorted()) {
            SortField[] fields = sort.map(it -> it.isAscending() ? DSL.field(it.getProperty()).asc()
                    : DSL.field(it.getProperty()).desc()).toList().toArray(new SortField[0]);
            step.orderBy(fields);
        }
    }

    private <S extends T> List<S> toList(ResultQuery<Record> rq) {
        try (ResultSet rs = rq.fetchResultSet()) {
            return (List<S>) jdbcMapper.stream(rs).collect(Collectors.toList());
        }
        catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    private <S extends T> Optional<S> toOne(ResultQuery<Record> rq) {
        try (ResultSet rs = rq.fetchResultSet()) {
            return Optional.ofNullable(DataAccessUtils.nullableSingleResult(((JdbcMapper<S>) jdbcMapper).stream(rs).collect(Collectors.toList())));
        }
        catch (SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }
}
