package cn.javaer.snippetsbox.springframework.data.jooq.jdbc;

import cn.javaer.snippetsbox.springframework.data.jooq.jdbc.config.EnableJooqJdbcRepositories;
import cn.javaer.snippetsbox.springframework.data.jooq.jdbc.config.JooqJdbcRepositoriesAutoConfiguration;
import cn.javaer.snippetsbox.springframework.data.jooq.jdbc.user.User;
import cn.javaer.snippetsbox.springframework.data.jooq.jdbc.user.UserJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SimpleJooqJdbcRepositoryTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqJdbcRepositoriesAutoConfiguration.class, JooqAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void testFind() {
        this.contextRunner.withUserConfiguration(SimpleJooqJdbcRepositoryTest.DataSourceConfiguration.class)
                .run(context -> {
                    final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    jdbcTemplate.execute("CREATE TABLE T_USER ( id  INTEGER IDENTITY PRIMARY KEY, name VARCHAR(30), gender VARCHAR(30) )");

                    final UserJdbcRepository userRepository = context.getBean(UserJdbcRepository.class);
                    userRepository.save(new User("name1", "man"));
                    userRepository.save(new User("name2", "man"));

                    final Iterable<User> users = userRepository.findAll();
                    assertThat(users).hasSize(2);

                    final Page<User> page = userRepository.findAll(PageRequest.of(0, 1));
                    assertThat(page).hasSize(1);

                    final Optional<User> one = userRepository.findOne(Example.of(new User("name1", "man"), ExampleMatcher.matchingAll()));
                    assertThat(one).isNotEmpty();

                    final Iterable<User> sortUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "name"));
                    final Iterator<User> iterator = sortUsers.iterator();
                    assertThat(iterator.next()).hasFieldOrPropertyWithValue("name", "name2");
                    assertThat(iterator.next()).hasFieldOrPropertyWithValue("name", "name1");
                });
    }

    @Configuration(proxyBeanMethods = false)
    @EnableJooqJdbcRepositories(basePackageClasses = User.class)
    static class DataSourceConfiguration {
        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }
}