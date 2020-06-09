package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import cn.javaer.snippets.box.spring.data.jooq.AbstractJooqRepository;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.UpdateConditionStep;
import org.jooq.impl.DSL;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.mapping.PersistentPropertyAccessor;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link JooqJdbcRepository} interface.
 *
 * @author cn-src
 */
@Transactional(readOnly = true)
public class SimpleJooqJdbcRepository<T, ID> extends AbstractJooqRepository<T, ID> implements JooqJdbcRepository<T, ID> {

    private final JdbcAggregateOperations entityOperations;
    private final JdbcOperations jdbcOperations;
    private final JdbcConverter jdbcConverter;
    private final Class<T> repositoryEntityClass;
    private final EntityRowMapper<T> repositoryEntityRowMapper;
    private final RelationalPersistentEntity<T> persistentEntity;

    public SimpleJooqJdbcRepository(final DSLContext dsl,
                                    final RelationalMappingContext context,
                                    final RelationalPersistentEntity<T> persistentEntity,
                                    final JdbcAggregateOperations entityOperations,
                                    final NamedParameterJdbcOperations jdbcOperations,
                                    final JdbcConverter jdbcConverter,
                                    final AuditorAware<?> auditorAware) {
        super(dsl, persistentEntity, context, auditorAware);
        this.entityOperations = entityOperations;
        this.jdbcConverter = jdbcConverter;
        this.jdbcOperations = jdbcOperations.getJdbcOperations();
        this.repositoryEntityClass = persistentEntity.getType();
        this.repositoryEntityRowMapper = new EntityRowMapper<>(persistentEntity, jdbcConverter);
        this.persistentEntity = persistentEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <S extends T> S save(final S instance) {
        return this.entityOperations.save(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <S extends T> Iterable<S> saveAll(final Iterable<S> entities) {

        return Streamable.of(entities).stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(final ID id) {
        return Optional.ofNullable(this.entityOperations.findById(id, this.repositoryEntityClass));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(final ID id) {
        return this.entityOperations.existsById(id, this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll() {
        return this.entityOperations.findAll(this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAllById(final Iterable<ID> ids) {
        return this.entityOperations.findAllById(ids, this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return this.entityOperations.count(this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteById(final ID id) {
        this.entityOperations.deleteById(id, this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(final T instance) {
        this.entityOperations.delete(instance, this.repositoryEntityClass);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public void deleteAll(final Iterable<? extends T> entities) {
        entities.forEach(it -> this.entityOperations.delete(it, (Class<T>) it.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAll() {
        this.entityOperations.deleteAll(this.repositoryEntityClass);
    }

    @Transactional
    @Override
    public T insert(final T instance) {
        return this.entityOperations.insert(instance);
    }

    @Transactional
    @Override
    public <S extends T> int[] batchInsert(final Iterable<S> entities) {
        Assert.notNull(entities, "Entities must not be null!");

        final StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(this.persistentEntity.getTableName());
        final StringJoiner columnJoiner = new StringJoiner(",", "(", ")");
        final StringJoiner valuesJoiner = new StringJoiner(",", "(", ")");

        int size = 0;
        for (final RelationalPersistentProperty persistentProperty : this.persistentEntity) {
            final String columnName = persistentProperty.getColumnName().getReference();
            columnJoiner.add(columnName);
            valuesJoiner.add("?");
            size++;
        }

        sqlBuilder.append(columnJoiner.toString())
                .append(" VALUES ")
                .append(valuesJoiner.toString());

        final List<Object[]> batchValues = new ArrayList<>();
        for (final S entity : entities) {
            final PersistentPropertyAccessor<S> propertyAccessor = this.persistentEntity.getPropertyAccessor(entity);
            final List<Object> values = new ArrayList<>(size);
            for (final RelationalPersistentProperty property : this.persistentEntity) {
                final Object value = propertyAccessor.getProperty(property);
                values.add(value);
            }
            batchValues.add(values.toArray());
        }
        return this.jdbcOperations.batchUpdate(sqlBuilder.toString(), batchValues);
    }

    @Transactional
    @Override
    public T update(final T instance) {
        return this.entityOperations.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll(final Sort sort) {
        return this.entityOperations.findAll(this.repositoryEntityClass, sort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(final Pageable pageable) {
        return this.entityOperations.findAll(this.repositoryEntityClass, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Optional<S> findOne(final Example<S> example) {
        final Query query = this.findWithExampleStep(example);
        try {
            //noinspection ConstantConditions
            return Optional.of(this.jdbcOperations.queryForObject(query.getSQL(), query.getBindValues().toArray(),
                    this.getEntityRowMapper(example.getProbeType())));
        }
        catch (final EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Iterable<S> findAll(final Example<S> example) {
        final Query query = this.findWithExampleStep(example);
        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.getEntityRowMapper(example.getProbeType()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Iterable<S> findAll(final Example<S> example, final Sort sort) {
        final Query query = this.findWithExampleAndSortStep(example, sort);
        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.getEntityRowMapper(example.getProbeType()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        final long count = this.count(example);
        if (count == 0) {
            return new PageImpl<>(Collections.emptyList());
        }
        final Query query = this.findWithExampleAndPageableStep(example, pageable);
        final List<S> entityList = this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.getEntityRowMapper(example.getProbeType()));
        return new PageImpl<>(entityList, pageable, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> long count(final Example<S> example) {
        final Query query = this.countWithExampleStep(example);
        //noinspection ConstantConditions
        return this.jdbcOperations.queryForObject(query.getSQL(), query.getBindValues().toArray(), Long.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> boolean exists(final Example<S> example) {
        return this.count(example) > 0;
    }

    @Override
    public Optional<T> findOne(final Condition condition) {
        final Query query = this.findWithConditionStep(condition);
        try {
            //noinspection ConstantConditions
            return Optional.of(this.jdbcOperations.queryForObject(query.getSQL(), query.getBindValues().toArray(),
                    this.repositoryEntityRowMapper));
        }
        catch (final EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    @Override
    public List<T> findAll(final Condition condition) {
        final Query query = this.findWithConditionStep(condition);

        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper);
    }

    @Override
    public Page<T> findAll(final Condition condition, final Pageable pageable) {
        final long count = this.count(condition);
        if (count == 0) {
            return new PageImpl<>(Collections.emptyList());
        }
        final Query query = this.findWithConditionAndPageableStep(condition, pageable);
        final List<T> list = this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper);
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public long count(final Condition condition) {
        final Query query = this.dsl.selectCount()
                .from(DSL.table(this.persistentEntity.getTableName().getReference()))
                .where(condition)
                .getQuery();
        //noinspection ConstantConditions
        return this.jdbcOperations.queryForObject(query.getSQL(), query.getBindValues().toArray(), Long.class);
    }

    @Override
    public boolean exists(final Condition condition) {
        return this.count(condition) > 0;
    }

    @Override
    public DSLContext dsl() {
        return this.dsl;
    }

    @Override
    public Optional<T> findByIdAndCreator(final ID id) {
        final Query query = this.findByIdAndCreatorStep(id);

        try {
            //noinspection ConstantConditions
            return Optional.of(this.jdbcOperations.queryForObject(query.getSQL(), query.getBindValues().toArray(),
                    this.repositoryEntityRowMapper));
        }
        catch (final EmptyResultDataAccessException emptyResultDataAccessException) {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<T> findAllByCreator() {

        final Query query = this.findAllByCreatorStep();

        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper);
    }

    @Override
    public Iterable<T> findAllByCreator(final Pageable pageable) {
        final Query query = this.findAllByCreatorStep(pageable);

        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper);
    }

    @Transactional
    @Override
    public T updateByIdAndCreator(final T instance) {
        final UpdateConditionStep<Record> updateStep = this.updateByIdAndCreatorStep(instance);
        this.jdbcOperations.update(updateStep.getSQL(), updateStep.getBindValues());
        return instance;
    }

    @Transactional
    @Override
    public void deleteByIdAndCreator(final ID id) {
        final DeleteConditionStep<Record> deleteStep = this.deleteByIdAndCreatorStep(id);
        this.jdbcOperations.update(deleteStep.getSQL(), deleteStep.getBindValues());
    }

    protected <E> EntityRowMapper<E> getEntityRowMapper(final Class<E> domainType) {
        return new EntityRowMapper<>(this.getRequiredPersistentEntity(domainType), this.jdbcConverter);
    }
}
