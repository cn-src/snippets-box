package cn.javaer.snippets.spring.data.jooq.jdbc.config;

import org.springframework.data.repository.config.RepositoryBeanDefinitionRegistrarSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;

import java.lang.annotation.Annotation;

/**
 * @author cn-src
 */
class JooqJdbcRepositoriesRegistrar extends RepositoryBeanDefinitionRegistrarSupport {
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return EnableJooqJdbcRepositories.class;
    }

    @Override
    protected RepositoryConfigurationExtension getExtension() {
        return new JooqJdbcRepositoryConfigExtension();
    }
}
