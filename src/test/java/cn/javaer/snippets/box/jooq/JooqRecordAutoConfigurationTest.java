package cn.javaer.snippets.box.jooq;

import cn.javaer.snippets.box.jooq.gen.tables.records.CityRecord;
import cn.javaer.snippets.box.jooq.spring.record.JooqRecordAttachAdvice;
import cn.javaer.snippets.spring.boot.autoconfigure.jooq.JooqRecordAutoConfiguration;
import org.jooq.DSLContext;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author cn-src
 */
class JooqRecordAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqRecordAutoConfiguration.class, JooqAutoConfiguration.class, JdbcTemplateAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, MockMvcAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void autoConfigure() {

        this.contextRunner.withUserConfiguration(TestConfiguration.class)
                .withBean(AssertController.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(DSLContext.class);
                    assertThat(context).hasSingleBean(JooqRecordAttachAdvice.class);
                    assertThat(context).hasSingleBean(AssertController.class);
                    final JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
                    jdbcTemplate.execute("CREATE TABLE city ( id  INTEGER IDENTITY PRIMARY KEY, name VARCHAR(30) )");

                    final MockMvc mockMvc = context.getBean(MockMvc.class);

                    mockMvc.perform(post("/").content("{\"id\":1,\"name\":\"jack\"}").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

                    mockMvc.perform(get("/1")).andExpect(status().isOk());
                });
    }

    @Test
    @Disabled
    void jooqCodegen() throws Exception {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(new TestConfiguration().dataSource());
        jdbcTemplate.execute("CREATE TABLE city ( id  INTEGER IDENTITY PRIMARY KEY, name VARCHAR(30) )");
        final org.jooq.meta.jaxb.Configuration configuration = new org.jooq.meta.jaxb.Configuration()
                .withJdbc(new Jdbc()
                        .withDriver("org.h2.Driver")
                        .withUrl("jdbc:h2:mem:test")
                        .withUser("sa")
                        .withPassword(""))
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName("org.jooq.meta.h2.H2Database")
                                .withInputSchema("PUBLIC")
                        )
                        .withTarget(new Target()
                                .withPackageName("cn.javaer.snippetsbox.jooq.gen")
                                .withDirectory(System.getProperty("user.dir") + "/src/test/java")));

        GenerationTool.generate(configuration);
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfiguration {
        @Bean
        DataSource dataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }

    @RestController
    static class AssertController {

        @PostMapping("/")
        static String save(@RequestBody final CityRecord updatableRecord) {
            assertThat(updatableRecord).isNotNull();
            assertThat(updatableRecord.getName()).isEqualTo("jack");
            updatableRecord.store();
            return "OK";
        }

        @GetMapping("/{id}")
        static String get(@PathVariable("id") final CityRecord updatableRecord) {
            updatableRecord.refresh();
            assertThat(updatableRecord.getName()).isEqualTo("jack");
            return "OK";
        }
    }
}