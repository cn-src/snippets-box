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
package cn.javaer.snippetsbox.springframework.boot.data.jooq.jdbc;

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
import org.springframework.data.jdbc.repository.RowMapperMap;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @author Jens Schauder
 * @author Greg Turnquist
 * @author Christoph Strobl
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public class JooqRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends TransactionalRepositoryFactoryBeanSupport<T, S, ID> implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private BeanFactory beanFactory;
    private RelationalMappingContext mappingContext;
    private JdbcConverter converter;
    private DataAccessStrategy dataAccessStrategy;
    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
    private NamedParameterJdbcOperations operations;
    private EntityCallbacks entityCallbacks;

    /**
     * Creates a new {@link JooqRepositoryFactoryBean} for the given repository interface.
     *
     * @param repositoryInterface must not be {@literal null}.
     */
    protected JooqRepositoryFactoryBean(final Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#setApplicationEventPublisher(org.springframework.context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {

        super.setApplicationEventPublisher(publisher);

        this.publisher = publisher;
    }

    /**
     * Creates the actual {@link RepositoryFactorySupport} instance.
     */
    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {

        final JooqRepositoryFactory jooqRepositoryFactory = new JooqRepositoryFactory(this.dataAccessStrategy, this.mappingContext,
                this.converter, this.publisher, this.operations, this.beanFactory.getBean(DSLContext.class));
        jooqRepositoryFactory.setQueryMappingConfiguration(this.queryMappingConfiguration);
        jooqRepositoryFactory.setEntityCallbacks(this.entityCallbacks);

        return jooqRepositoryFactory;
    }

    @Autowired
    protected void setMappingContext(final RelationalMappingContext mappingContext) {

        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    /**
     * @param dataAccessStrategy can be {@literal null}.
     */
    public void setDataAccessStrategy(final DataAccessStrategy dataAccessStrategy) {
        this.dataAccessStrategy = dataAccessStrategy;
    }

    /**
     * @param queryMappingConfiguration can be {@literal null}. {@link #afterPropertiesSet()} defaults to
     *         {@link QueryMappingConfiguration#EMPTY} if {@literal null}.
     */
    @Autowired(required = false)
    public void setQueryMappingConfiguration(final QueryMappingConfiguration queryMappingConfiguration) {
        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    /**
     * @param rowMapperMap can be {@literal null}. {@link #afterPropertiesSet()} defaults to {@link RowMapperMap#EMPTY} if
     *         {@literal null}.
     *
     * @deprecated use {@link #setQueryMappingConfiguration(QueryMappingConfiguration)} instead.
     */
    @Deprecated
    @Autowired(required = false)
    public void setRowMapperMap(final RowMapperMap rowMapperMap) {
        this.setQueryMappingConfiguration(rowMapperMap);
    }

    public void setJdbcOperations(final NamedParameterJdbcOperations operations) {
        this.operations = operations;
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

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {

        Assert.state(this.mappingContext != null, "MappingContext is required and must not be null!");
        Assert.state(this.converter != null, "RelationalConverter is required and must not be null!");

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