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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.QueryMappingConfiguration;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.io.Serializable;

/**
 * Special adapter for Springs {@link org.springframework.beans.factory.FactoryBean} interface to allow easy setup of
 * repository factories via Spring configuration.
 *
 * @author cn-src
 */
public class JooqRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends JdbcRepositoryFactoryBean<T, S, ID> {

    private ApplicationEventPublisher publisher;
    private BeanFactory beanFactory;
    private RelationalMappingContext mappingContext;
    private JdbcConverter converter;
    private DataAccessStrategy dataAccessStrategy;
    private QueryMappingConfiguration queryMappingConfiguration = QueryMappingConfiguration.EMPTY;
    private NamedParameterJdbcOperations operations;
    private EntityCallbacks entityCallbacks;
    private DSLContext dslContext;

    protected JooqRepositoryFactoryBean(final Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport doCreateRepositoryFactory() {
        final JooqRepositoryFactory jdbcRepositoryFactory = new JooqRepositoryFactory(this.dataAccessStrategy, this.mappingContext,
                this.converter, this.publisher, this.operations, this.dslContext);
        jdbcRepositoryFactory.setQueryMappingConfiguration(this.queryMappingConfiguration);
        jdbcRepositoryFactory.setEntityCallbacks(this.entityCallbacks);
        return jdbcRepositoryFactory;
    }

    @Override
    public void setApplicationEventPublisher(final ApplicationEventPublisher publisher) {
        super.setApplicationEventPublisher(publisher);
        this.publisher = publisher;
    }

    @Override
    protected void setMappingContext(final RelationalMappingContext mappingContext) {
        super.setMappingContext(mappingContext);
        this.mappingContext = mappingContext;
    }

    @Override
    public void setDataAccessStrategy(final DataAccessStrategy dataAccessStrategy) {
        super.setDataAccessStrategy(dataAccessStrategy);
        this.dataAccessStrategy = dataAccessStrategy;
    }

    @Override
    public void setQueryMappingConfiguration(final QueryMappingConfiguration queryMappingConfiguration) {
        super.setQueryMappingConfiguration(queryMappingConfiguration);
        this.queryMappingConfiguration = queryMappingConfiguration;
    }

    @Override
    public void setJdbcOperations(final NamedParameterJdbcOperations operations) {
        super.setJdbcOperations(operations);
        this.operations = operations;
    }

    @Override
    public void setConverter(final JdbcConverter converter) {
        super.setConverter(converter);
        this.converter = converter;
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.entityCallbacks = EntityCallbacks.create(this.beanFactory);
        this.dslContext = this.beanFactory.getBean(DSLContext.class);
    }
}
