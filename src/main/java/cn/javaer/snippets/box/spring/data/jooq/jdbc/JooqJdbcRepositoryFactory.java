package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * Creates repository implementation based on JDBC.
 *
 * @author cn-src
 */
public class JooqJdbcRepositoryFactory extends RepositoryFactorySupport {

    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final ApplicationEventPublisher publisher;
    private final DataAccessStrategy accessStrategy;
    private final NamedParameterJdbcOperations operations;

    private final Dialect dialect;

    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;

    private EntityCallbacks entityCallbacks;

    private final DSLContext dslContext;
    private final AuditorAware<?> auditorAware;

    Constructor<? extends QueryLookupStrategy> constructor;

    public JooqJdbcRepositoryFactory(final DataAccessStrategy dataAccessStrategy, final RelationalMappingContext context,
                                     final JdbcConverter converter, final Dialect dialect, final ApplicationEventPublisher publisher,
                                     final NamedParameterJdbcOperations operations, final DSLContext dslContext,
                                     final AuditorAware<?> auditorAware) {
        Assert.notNull(dataAccessStrategy, "DataAccessStrategy must not be null!");
        Assert.notNull(context, "RelationalMappingContext must not be null!");
        Assert.notNull(converter, "RelationalConverter must not be null!");
        Assert.notNull(dialect, "Dialect must not be null!");
        Assert.notNull(publisher, "ApplicationEventPublisher must not be null!");

        Assert.notNull(dslContext, "DSLContext must not be null!");

        this.publisher = publisher;
        this.context = context;
        this.converter = converter;
        this.dialect = dialect;
        this.accessStrategy = dataAccessStrategy;
        this.operations = operations;

        this.dslContext = dslContext;
        this.auditorAware = auditorAware;
        try {
            //noinspection unchecked
            this.constructor = (Constructor<? extends QueryLookupStrategy>) Class.forName("org.springframework.data.jdbc.repository.support.JdbcQueryLookupStrategy.JdbcQueryLookupStrategy")
                    .getDeclaredConstructor(ApplicationEventPublisher.class,
                            EntityCallbacks.class,
                            RelationalMappingContext.class,
                            JdbcConverter.class,
                            Dialect.class,
                            QueryMappingConfiguration.class,
                            NamedParameterJdbcOperations.class);
        }
        catch (final NoSuchMethodException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setQueryMappingConfiguration(final QueryMappingConfiguration queryMappingConfiguration) {

        Assert.notNull(queryMappingConfiguration, "QueryMappingConfiguration must not be null!");

        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(final Class<T> aClass) {

        final RelationalPersistentEntity<?> entity = this.context.getRequiredPersistentEntity(aClass);

        return (EntityInformation<T, ID>) new PersistentEntityInformation<>(entity);
    }

    @Override
    protected Object getTargetRepository(final RepositoryInformation repositoryInformation) {

        final JdbcAggregateTemplate template = new JdbcAggregateTemplate(this.publisher, this.context, this.converter, this.accessStrategy);

        final SimpleJooqJdbcRepository<?, Object> repository = new SimpleJooqJdbcRepository<>(
                this.dslContext, this.context,
                this.context.getRequiredPersistentEntity(repositoryInformation.getDomainType()),
                template, this.operations, this.converter, this.auditorAware
        );

        if (this.entityCallbacks != null) {
            template.setEntityCallbacks(this.entityCallbacks);
        }

        return repository;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(final RepositoryMetadata repositoryMetadata) {
        return SimpleJooqJdbcRepository.class;
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(@Nullable final QueryLookupStrategy.Key key,
                                                                   final QueryMethodEvaluationContextProvider evaluationContextProvider) {
        try {
            return Optional.of(this.constructor.newInstance(this.publisher, this.entityCallbacks, this.context, this.converter, this.dialect,
                    this.queryMappingConfiguration, this.operations));
        }
        catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setEntityCallbacks(final EntityCallbacks entityCallbacks) {
        this.entityCallbacks = entityCallbacks;
    }
}
