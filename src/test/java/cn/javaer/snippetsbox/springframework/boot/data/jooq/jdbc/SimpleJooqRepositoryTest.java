package cn.javaer.snippetsbox.springframework.boot.data.jooq.jdbc;

import cn.javaer.snippetsbox.springframework.boot.data.jooq.jdbc.user.User;
import cn.javaer.snippetsbox.springframework.boot.data.jooq.jdbc.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SimpleJooqRepositoryTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, JdbcRepositoriesAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void testFind() {
        contextRunner.withUserConfiguration(SimpleJooqRepositoryTest.DataSourceConfiguration.class)
                .run(context -> {
                    JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    jdbcTemplate.execute("CREATE TABLE T_USER ( id  INTEGER IDENTITY PRIMARY KEY, name VARCHAR(30), gender VARCHAR(30) )");

                    UserRepository userRepository = context.getBean(UserRepository.class);
                    userRepository.save(new User("name1", "man"));
                    userRepository.save(new User("name2", "man"));

                    Iterable<User> users = userRepository.findAll();
                    assertThat(users).hasSize(2);

                    Page<User> page = userRepository.findAll(PageRequest.of(0, 1));
                    assertThat(page).hasSize(1);

                    Optional<User> one = userRepository.findOne(Example.of(new User("name1", "man"), ExampleMatcher.matchingAll()));
                    assertThat(one).isNotEmpty();

                    Iterable<User> sortUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "name"));
                    Iterator<User> iterator = sortUsers.iterator();
                    assertThat(iterator.next()).hasFieldOrPropertyWithValue("name", "name2");
                    assertThat(iterator.next()).hasFieldOrPropertyWithValue("name", "name1");
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableJdbcRepositories(repositoryFactoryBeanClass = JooqRepositoryFactoryBean.class, basePackageClasses = User.class)
    static class DataSourceConfiguration extends AbstractJdbcConfiguration {
        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }
}