package cn.javaer.snippetsbox.springframework.data.jooq.jdbc;

import cn.javaer.snippetsbox.springframework.data.jooq.AbstractJooqRepository;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.simpleflatmapper.jooq.SelectQueryMapper;
import org.simpleflatmapper.jooq.SelectQueryMapperFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.EntityRowMapper;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link JooqJdbcRepository} interface.
 *
 * @author cn-src
 */
@Transactional(readOnly = true)
public class SimpleJooqJdbcRepository<T, ID> extends AbstractJooqRepository<T, ID> implements JooqJdbcRepository<T, ID> {

    private final SelectQueryMapper<T> queryMapper;
    private final JdbcAggregateOperations entityOperations;
    private final JdbcOperations jdbcOperations;
    private final JdbcConverter jdbcConverter;
    private final Class<T> repositoryEntityClass;
    private final EntityRowMapper<T> repositoryEntityRowMapper;

    public SimpleJooqJdbcRepository(final DSLContext dsl,
                                    final RelationalMappingContext context,
                                    final RelationalPersistentEntity<T> persistentEntity,
                                    final JdbcAggregateOperations entityOperations,
                                    final NamedParameterJdbcOperations jdbcOperations,
                                    final JdbcConverter jdbcConverter) {
        super(dsl, persistentEntity, context);
        this.entityOperations = entityOperations;
        this.jdbcConverter = jdbcConverter;
        this.jdbcOperations = jdbcOperations.getJdbcOperations();
        this.queryMapper = SelectQueryMapperFactory.newInstance().ignorePropertyNotFound().newMapper(persistentEntity.getType());
        this.repositoryEntityClass = persistentEntity.getType();
        this.repositoryEntityRowMapper = new EntityRowMapper<>(persistentEntity, jdbcConverter);
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
    @Transactional
    @Override
    public void deleteAll(final Iterable<? extends T> entities) {
        //noinspection unchecked
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll(final Sort sort) {
        final Query query = this.findWithSortStep(sort);
        return this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(final Pageable pageable) {
        final Query query = this.findWithPageableStep(pageable);
        return new PageImpl<>(this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.repositoryEntityRowMapper), pageable, this.count());
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
        final Query query = this.findWithExampleAndPageableStep(example, pageable);
        final List<S> entityList = this.jdbcOperations.query(query.getSQL(), query.getBindValues().toArray(),
                this.getEntityRowMapper(example.getProbeType()));
        return new PageImpl<>(entityList, pageable, this.count(example));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> long count(final Example<S> example) {
        final Query query = this.countWithExampleStep(example);
        //noinspection ConstantConditions
        return this.jdbcOperations.queryForObject(query.getSQL(), Long.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> boolean exists(final Example<S> example) {
        return this.count(example) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(final QueryStep queryStep) {
        return this.queryMapper.asList(queryStep.step(this.dsl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findOne(final QueryStep queryStep) {
        return Optional.ofNullable(DataAccessUtils.nullableSingleResult(this.queryMapper.asList(queryStep.step(this.dsl))));
    }

    protected <E> EntityRowMapper<E> getEntityRowMapper(final Class<E> domainType) {
        return new EntityRowMapper<>(this.getRequiredPersistentEntity(domainType), this.jdbcConverter);
    }
}
