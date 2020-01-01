package cn.javaer.snippetsbox.springframework.data.jooq.jdbc;

import org.jooq.DSLContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.DefaultDataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.convert.SqlGeneratorSource;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author cn-src
 * @see JdbcRepositoryFactoryBean
 */
public class JooqJdbcRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private BeanFactory beanFactory;
    private RelationalMappingContext mappingContext;
    private JdbcConverter converter;
    private DataAccessStrategy dataAccessStrategy;
    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
    private NamedParameterJdbcOperations operations;
    private EntityCallbacks entityCallbacks;

    private DSLContext dslContext;

    protected JooqJdbcRepositoryFactoryBean(final Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {

        super.setApplicationEventPublisher(publisher);

        this.publisher = publisher;
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        final JooqJdbcRepositoryFactory jdbcRepositoryFactory = new JooqJdbcRepositoryFactory(this.dataAccessStrategy, this.mappingContext,
                this.converter, this.publisher, this.operations, this.dslContext);
        jdbcRepositoryFactory.setQueryMappingConfiguration(this.queryMappingConfiguration);
        jdbcRepositoryFactory.setEntityCallbacks(this.entityCallbacks);

        return jdbcRepositoryFactory;
    }

    @Autowired
    protected void setMappingContext(final RelationalMappingContext mappingContext) {

        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    public void setDataAccessStrategy(final DataAccessStrategy dataAccessStrategy) {
        this.dataAccessStrategy = dataAccessStrategy;
    }

    @Autowired(required = false)
    public void setQueryMappingConfiguration(final QueryMappingConfiguration queryMappingConfiguration) {
        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    public void setJdbcOperations(final NamedParameterJdbcOperations operations) {
        this.operations = operations;
    }

    public void setDslContext(final DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Autowired
    public void setConverter(final JdbcConverter converter) {
        this.converter = converter;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {

        super.setBeanFactory(beanFactory);

        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() {

        Assert.state(this.mappingContext != null, "MappingContext is required and must not be null!");
        Assert.state(this.converter != null, "RelationalConverter is required and must not be null!");

        if (this.dslContext == null) {

            Assert.state(this.beanFactory != null, "If no DSLContext are set a BeanFactory must be available.");

            this.dslContext = this.beanFactory.getBean(DSLContext.class);
        }

        if (this.operations == null) {

            Assert.state(this.beanFactory != null, "If no JdbcOperations are set a BeanFactory must be available.");

            this.operations = this.beanFactory.getBean(NamedParameterJdbcOperations.class);
        }

        if (this.dataAccessStrategy == null) {

            Assert.state(this.beanFactory != null, "If no DataAccessStrategy is set a BeanFactory must be available.");

            this.dataAccessStrategy = this.beanFactory.getBeanProvider(DataAccessStrategy.class)
                    .getIfAvailable(() -> {

                        final SqlGeneratorSource sqlGeneratorSource = new SqlGeneratorSource(this.mappingContext);
                        return new DefaultDataAccessStrategy(sqlGeneratorSource, this.mappingContext, this.converter,
                                this.operations);
                    });
        }

        if (this.queryMappingConfiguration == null) {
            this.queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
        }

        if (this.beanFactory != null) {
            this.entityCallbacks = EntityCallbacks.create(this.beanFactory);
        }

        super.afterPropertiesSet();
    }
}
