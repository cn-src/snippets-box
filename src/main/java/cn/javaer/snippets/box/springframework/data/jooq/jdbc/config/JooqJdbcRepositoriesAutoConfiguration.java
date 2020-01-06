package cn.javaer.snippets.box.springframework.data.jooq.jdbc.config;

import org.jooq.DSLContext;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author cn-src
 * @see EnableJooqJdbcRepositories
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean({NamedParameterJdbcOperations.class, PlatformTransactionManager.class})
@ConditionalOnClass({DSLContext.class, NamedParameterJdbcOperations.class, AbstractJdbcConfiguration.class})
@ConditionalOnProperty(prefix = "spring.data.jooq.jdbc.repositories", name = "enabled", havingValue = "true",
        matchIfMissing = true)
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
public class JooqJdbcRepositoriesAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(JooqJdbcRepositoryConfigExtension.class)
    @Import(RepositoriesRegistrar.class)
    static class JooqJdbcRepositoriesConfiguration {

    }

    @Configuration
    @ConditionalOnMissingBean({AbstractJdbcConfiguration.class})
    static class SpringBootJooqJdbcConfiguration extends AbstractJdbcConfiguration {

    }
}
