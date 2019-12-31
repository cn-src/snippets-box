package cn.javaer.snippetsbox.springframework.data.jooq.jdbc.config;

import cn.javaer.snippetsbox.springframework.data.jooq.jdbc.JooqJdbcRepositoryFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

/**
 * @author cn-src
 */
public class JooqJdbcRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

    @Override
    public String getModuleName() {
        return "JOOQ_JDBC";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return JooqJdbcRepositoryFactoryBean.class.getName();
    }

    @Override
    protected String getModulePrefix() {
        return this.getModuleName().toLowerCase(Locale.US);
    }

    @Override
    public void postProcess(final BeanDefinitionBuilder builder, final RepositoryConfigurationSource source) {

        source.getAttribute("jdbcOperationsRef")
                .filter(StringUtils::hasText)
                .ifPresent(s -> builder.addPropertyReference("jdbcOperations", s));

        source.getAttribute("dataAccessStrategyRef")
                .filter(StringUtils::hasText)
                .ifPresent(s -> builder.addPropertyReference("dataAccessStrategy", s));

        source.getAttribute("dslContextRef")
                .filter(StringUtils::hasText)
                .ifPresent(s -> builder.addPropertyReference("dslContext", s));
    }

    /**
     * In strict mode only domain types having a {@link Table} annotation get a repository.
     */
    @Override
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Collections.singleton(Table.class);
    }
}
