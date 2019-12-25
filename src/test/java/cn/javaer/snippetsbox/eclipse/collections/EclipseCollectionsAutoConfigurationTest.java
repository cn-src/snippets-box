package cn.javaer.snippetsbox.eclipse.collections;

import cn.javaer.snippetsbox.eclipse.collections.city.City;
import cn.javaer.snippetsbox.eclipse.collections.city.CityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactoryBean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JdbcRepositoriesAutoConfiguration}.
 *
 * @author cn-src
 */
class EclipseCollectionsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void basicAutoConfiguration() {
        this.contextRunner.withUserConfiguration(DataSourceConfiguration.class, EclipseCollectionsAutoConfiguration.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(CityRepository.class);
                    final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    jdbcTemplate.execute("CREATE TABLE city ( id  INTEGER IDENTITY PRIMARY KEY, name VARCHAR(30) )");

                    final CityRepository cityRepository = context.getBean(CityRepository.class);
                    cityRepository.save(new City("name"));
                    assertThat(cityRepository.findAll()).hasSize(1);
                });
    }

    @Configuration(proxyBeanMethods = false)
    // TODO repositoryFactoryBeanClass 不指定会无法注册 CityRepository
    @EnableJdbcRepositories(repositoryFactoryBeanClass = JdbcRepositoryFactoryBean.class, basePackageClasses = City.class)
    static class DataSourceConfiguration {

        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }
}
