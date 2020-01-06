package cn.javaer.snippets.box.simpleflatmapper.jooq;

import cn.javaer.snippets.spring.boot.autoconfigure.simpleflatmapper.jooq.SfmJooqAutoConfiguration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.junit.jupiter.api.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordUnmapperProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SfmJooqAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqAutoConfiguration.class, SfmJooqAutoConfiguration.class))
            .withPropertyValues("spring.datasource.name:test");

    @Test
    void dataSource() {
        this.contextRunner.withUserConfiguration(JooqDataSourceConfiguration.class)
                .run(context -> assertThat(context.getBeansOfType(DSLContext.class))
                        .isNotEmpty());
    }

    @Test
    void sfmRecordMapperProvider() {
        this.contextRunner.withUserConfiguration(JooqDataSourceConfiguration.class)
                .run(context -> {
                    final DSLContext dsl = context.getBean(DSLContext.class);
                    assertThat(dsl.configuration().dialect()).isEqualTo(SQLDialect.H2);
                    assertThat(dsl.configuration().recordMapperProvider()).isInstanceOf(SfmRecordMapperProvider.class);
                    assertThat(dsl.configuration().recordUnmapperProvider()).isInstanceOf(SfmRecordUnmapperProvider.class);
                });
    }

    @Configuration(proxyBeanMethods = false)
    static class JooqDataSourceConfiguration {
        @Bean
        DataSource jooqDataSource() {
            return DataSourceBuilder.create().url("jdbc:h2:mem:test").username("sa").build();
        }
    }
}
