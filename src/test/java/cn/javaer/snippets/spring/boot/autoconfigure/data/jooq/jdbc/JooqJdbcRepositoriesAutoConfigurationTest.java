package cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc;

import cn.javaer.snippets.TestAutoConfigurationPackage;
import cn.javaer.snippets.TestPostgreSQL;
import cn.javaer.snippets.empty.EmptyDataPackage;
import cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc.pojo.Demo;
import cn.javaer.snippets.spring.data.jooq.jdbc.config.EnableJooqJdbcRepositories;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class JooqJdbcRepositoriesAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqJdbcRepositoriesAutoConfiguration.class,
                    JooqAutoConfiguration.class,
                    JdbcTemplateAutoConfiguration.class,
                    DataSourceAutoConfiguration.class,
                    DataSourceTransactionManagerAutoConfiguration.class));

    @Test
    void jsonbTest() {
        this.contextRunner
                .withUserConfiguration(EnableRepositoriesConfiguration.class)
                .run(context -> {
                    final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    //language=PostgreSQL
                    jdbcTemplate.execute("CREATE TABLE demo (id bigserial NOT NULL" +
                            " CONSTRAINT demo_pkey" +
                            " PRIMARY KEY, jsonb1 jsonb,jsonb2 jsonb)");
                    final DemoRepository repository = context.getBean(DemoRepository.class);
                    //language=JSON
                    final JSONB jsonb1 = JSONB.valueOf("{\"k1\":1}");
                    final ObjectNode jsonb2 = new ObjectNode(new JsonNodeFactory(true));
                    jsonb2.put("k2", "v2");
                    repository.save(new Demo(jsonb1, jsonb2));
                    final Iterable<Demo> demos = repository.findAll();
                    assertThat(demos).hasSize(1)
                            .extracting(Demo::getJsonb1, Demo::getJsonb2);
                });
    }

    @TestAutoConfigurationPackage(EmptyDataPackage.class)
    @EnableJooqJdbcRepositories(basePackageClasses = DemoRepository.class)
    static class EnableRepositoriesConfiguration {
        @Bean
        DataSource dataSource() {
            return TestPostgreSQL.createDataSource();
        }
    }
}