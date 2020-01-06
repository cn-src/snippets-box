package cn.javaer.snippets.box.springframework.data.jooq.jdbc;

import cn.javaer.snippets.box.TestAutoConfigurationPackage;
import cn.javaer.snippets.box.empty.EmptyDataPackage;
import cn.javaer.snippets.box.springframework.data.jooq.jdbc.config.EnableJooqJdbcRepositories;
import cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc.JooqJdbcRepositoriesAutoConfiguration;
import cn.javaer.snippets.box.springframework.data.jooq.jdbc.user.User;
import cn.javaer.snippets.box.springframework.data.jooq.jdbc.user.UserJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SimpleJooqJdbcRepositoryTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqJdbcRepositoriesAutoConfiguration.class));

    @Test
    void testFind() {
        this.contextRunner.with(this.database())
                .withConfiguration(AutoConfigurations.of(
                        JooqAutoConfiguration.class,
                        JdbcTemplateAutoConfiguration.class,
                        DataSourceTransactionManagerAutoConfiguration.class))
                .withUserConfiguration(EnableRepositoriesConfiguration.class)
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

    private Function<ApplicationContextRunner, ApplicationContextRunner> database() {
        return (runner) -> runner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class))
                .withPropertyValues("spring.datasource.name:test");
    }

    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    @EnableJooqJdbcRepositories(basePackageClasses = User.class)
    static class EnableRepositoriesConfiguration {
    }
}