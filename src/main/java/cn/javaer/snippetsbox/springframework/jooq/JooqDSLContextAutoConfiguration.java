package cn.javaer.snippetsbox.springframework.jooq;

import org.jooq.DSLContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jooq.JooqProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 将 jOOQ 仅用于 SQL 生成时的自动配置（即：没有数据源）.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DSLContext.class)
@ConditionalOnMissingBean(DSLContext.class)
@EnableConfigurationProperties(JooqProperties.class)
public class JooqDSLContextAutoConfiguration {

    @Bean
    public DefaultDSLContext dslContext(final org.jooq.Configuration configuration) {
        return new DefaultDSLContext(configuration);
    }

    @Bean
    @ConditionalOnMissingBean(org.jooq.Configuration.class)
    public DefaultConfiguration jooqConfiguration(final JooqProperties properties,
                                                  final ObjectProvider<Settings> settings) {
        final DefaultConfiguration configuration = new DefaultConfiguration();
        configuration.setSQLDialect(properties.getSqlDialect());
        settings.ifAvailable(configuration::set);
        return configuration;
    }
}
