package cn.javaer.snippets.box.eclipse.collections;

import cn.javaer.snippets.box.TestAutoConfigurationPackage;
import cn.javaer.snippets.box.eclipse.collections.city.City;
import cn.javaer.snippets.box.eclipse.collections.city.CityRepository;
import cn.javaer.snippets.box.empty.EmptyDataPackage;
import cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.EclipseCollectionsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
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
            .withConfiguration(AutoConfigurations.of(EclipseCollectionsAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void basicAutoConfiguration() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(
                DataSourceAutoConfiguration.class,
                JdbcTemplateAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                JdbcRepositoriesAutoConfiguration.class))
                .withUserConfiguration(DataSourceConfiguration.class)
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
    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    @EnableJdbcRepositories(basePackageClasses = City.class)
    static class DataSourceConfiguration {

        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }
}
