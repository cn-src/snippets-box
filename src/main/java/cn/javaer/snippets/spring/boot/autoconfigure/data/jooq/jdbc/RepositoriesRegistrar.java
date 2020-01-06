package cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc;

import cn.javaer.snippets.box.springframework.data.jooq.jdbc.config.EnableJooqJdbcRepositories;
import cn.javaer.snippets.box.springframework.data.jooq.jdbc.config.JooqJdbcRepositoryConfigExtension;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author cn-src
 */
class RepositoriesRegistrar extends AbstractRepositoryConfigurationSourceSupport {

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJooqJdbcRepositories.class;
    }

    @Override
    protected Class<?> getConfiguration() {
        return EnableJooqJdbcRepositoriesConfiguration.class;
    }

    @Override
    protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
        return new JooqJdbcRepositoryConfigExtension();
    }

    @EnableJooqJdbcRepositories
    private static class EnableJooqJdbcRepositoriesConfiguration {

    }
}
