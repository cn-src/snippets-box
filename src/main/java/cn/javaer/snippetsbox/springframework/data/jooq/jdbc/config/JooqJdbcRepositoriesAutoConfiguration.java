/*
 * Copyright 2012-2019 the original author or authors.
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

package cn.javaer.snippetsbox.springframework.data.jooq.jdbc.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author cn-src
 * @see EnableJooqJdbcRepositories
 */
@SuppressWarnings("deprecation")
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({NamedParameterJdbcOperations.class, PlatformTransactionManager.class})
@ConditionalOnClass({NamedParameterJdbcOperations.class, AbstractJdbcConfiguration.class})
@ConditionalOnProperty(prefix = "spring.data.jooq.jdbc.repositories", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class JooqJdbcRepositoriesAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(JooqJdbcRepositoryConfigExtension.class)
    @Import(JooqJdbcRepositoriesRegistrar.class)
    static class JooqJdbcRepositoriesConfiguration {

    }

    @Configuration
    @ConditionalOnMissingBean({AbstractJdbcConfiguration.class, JdbcConfiguration.class})
    static class SpringBootJooqJdbcConfiguration extends AbstractJdbcConfiguration {

    }
}
