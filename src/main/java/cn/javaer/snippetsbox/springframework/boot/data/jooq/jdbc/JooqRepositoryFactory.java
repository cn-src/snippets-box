package cn.javaer.snippetsbox.springframework.boot.data.jooq.jdbc;

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

/**
 * Creates repository implementation based on JDBC and jOOQ.
 *
 * @author cn-src
 */
public class JooqRepositoryFactory extends JdbcRepositoryFactory {

    private final ApplicationEventPublisher publisher;
    private final RelationalMappingContext context;
    private final JdbcConverter converter;
    private final DataAccessStrategy accessStrategy;
    private EntityCallbacks entityCallbacks;

    private final DSLContext dsl;

    JooqRepositoryFactory(final DataAccessStrategy dataAccessStrategy, final RelationalMappingContext context, final JdbcConverter converter, final ApplicationEventPublisher publisher, final NamedParameterJdbcOperations operations, final DSLContext dsl) {
        super(dataAccessStrategy, context, converter, publisher, operations);
        this.publisher = publisher;
        this.context = context;
        this.converter = converter;
        this.accessStrategy = dataAccessStrategy;

        this.dsl = dsl;
    }

    @Override
    protected Object getTargetRepository(final RepositoryInformation repositoryInformation) {

        final JdbcAggregateTemplate template = new JdbcAggregateTemplate(this.publisher, this.context, this.converter, this.accessStrategy);

        final SimpleJooqRepository<?, Object> repository = new SimpleJooqRepository<>(template,
                this.context.getRequiredPersistentEntity(repositoryInformation.getDomainType()),
                this.dsl, this.context);

        if (this.entityCallbacks != null) {
            template.setEntityCallbacks(this.entityCallbacks);
        }

        return repository;
    }

    @Override
    public void setEntityCallbacks(final EntityCallbacks entityCallbacks) {
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(final RepositoryMetadata repositoryMetadata) {
        return SimpleJooqRepository.class;
    }
}
