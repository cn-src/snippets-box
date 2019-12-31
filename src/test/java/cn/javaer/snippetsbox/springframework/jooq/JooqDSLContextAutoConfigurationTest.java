package cn.javaer.snippetsbox.springframework.jooq;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class JooqDSLContextAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JooqDSLContextAutoConfiguration.class))
            .withPropertyValues("spring.jooq.sql-dialect:h2");

    @Test
    void buildSql() {
        this.contextRunner.run(context -> {
            final DSLContext dsl = context.getBean(DSLContext.class);
            final String sql = dsl.select(DSL.field("col1"), DSL.field("col2")).from(DSL.table("demo")).getSQL();
            assertThat(sql).isEqualTo("select col1, col2 from demo");
        });
    }
}