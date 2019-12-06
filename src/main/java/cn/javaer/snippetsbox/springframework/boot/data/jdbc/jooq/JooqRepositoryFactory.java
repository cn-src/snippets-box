package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

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

    JooqRepositoryFactory(DataAccessStrategy dataAccessStrategy, RelationalMappingContext context, JdbcConverter converter, ApplicationEventPublisher publisher, NamedParameterJdbcOperations operations, DSLContext dsl) {
        super(dataAccessStrategy, context, converter, publisher, operations);
        this.publisher = publisher;
        this.context = context;
        this.converter = converter;
        accessStrategy = dataAccessStrategy;

        this.dsl = dsl;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {

        JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, accessStrategy);

        SimpleJooqRepository<?, Object> repository = new SimpleJooqRepository<>(template,
                context.getRequiredPersistentEntity(repositoryInformation.getDomainType()),
                dsl, context);

        if (entityCallbacks != null) {
            template.setEntityCallbacks(entityCallbacks);
        }

        return repository;
    }

    @Override
    public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata repositoryMetadata) {
        return SimpleJooqRepository.class;
    }
}
