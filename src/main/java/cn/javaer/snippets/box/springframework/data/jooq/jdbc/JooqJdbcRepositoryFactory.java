package cn.javaer.snippets.box.springframework.data.jooq.jdbc;

import org.jooq.DSLContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

/**
 * Creates repository implementation based on JDBC.
 *
 * @author cn-src
 */
public class JooqJdbcRepositoryFactory extends JdbcRepositoryFactory {

    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final ApplicationEventPublisher publisher;
    private final DataAccessStrategy accessStrategy;
    private final NamedParameterJdbcOperations operations;

    private EntityCallbacks entityCallbacks;

    private final DSLContext dslContext;

    public JooqJdbcRepositoryFactory(final DataAccessStrategy dataAccessStrategy, final RelationalMappingContext context,
                                     final JdbcConverter converter, final ApplicationEventPublisher publisher,
                                     final NamedParameterJdbcOperations operations, final DSLContext dslContext) {

        super(dataAccessStrategy, context, converter, publisher, operations);
        Assert.notNull(dslContext, "DSLContext must not be null!");

        this.publisher = publisher;
        this.context = context;
        this.converter = converter;
        this.accessStrategy = dataAccessStrategy;
        this.operations = operations;
        this.dslContext = dslContext;
    }

    @Override
    protected Object getTargetRepository(final RepositoryInformation repositoryInformation) {

        final JdbcAggregateTemplate template = new JdbcAggregateTemplate(this.publisher, this.context, this.converter, this.accessStrategy);

        final SimpleJooqJdbcRepository<?, Object> repository = new SimpleJooqJdbcRepository<>(
                this.dslContext, this.context,
                this.context.getRequiredPersistentEntity(repositoryInformation.getDomainType()),
                template, this.operations, this.converter
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
    public void setEntityCallbacks(final EntityCallbacks entityCallbacks) {
        super.setEntityCallbacks(entityCallbacks);
        this.entityCallbacks = entityCallbacks;
    }
}
