package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class ConditionCreatorTest {
    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

    @Test
    void create() {
        final Condition condition = ConditionCreator.create(new Query2("str1", "str2"));
        assertThat(this.dsl.render(condition))
                .isEqualTo("(str1 = ? and cast(str2 as varchar) like ('%' || replace(replace(replace(?, '!', '!!'), '%', '!%'), '_', '!_') || '%') escape '!')");
    }

    @Test
    void createWithIgnoreUnannotated() {
        final Condition condition = ConditionCreator.createWithIgnoreUnannotated(new Query1("demo", "demo",
                JSONB.valueOf("{\"k\":\"v\"}"),
                JSONB.valueOf("{\"k\":\"v\"}")));

        assertThat(this.dsl.render(condition))
                .isEqualTo("((jsonb2::jsonb @> cast(? as jsonb)::jsonb) and cast(str2 as varchar) like ('%' || replace(replace(replace(?, '!', '!!'), '%', '!%'), '_', '!_') || '%') escape '!')");
    }

    @Test
    void conditionBetween() {
        final Condition condition = ConditionCreator.create(new Query3(1, 4));
        assertThat(this.dsl.render(condition)).isEqualTo("col_num between ? and ?");
    }

    @Test
    void conditionDateBetween() {
        final Condition condition = ConditionCreator.create(new Query4(LocalDate.of(2020, 1, 11), LocalDate.of(2020, 4, 11)));
        assertThat(this.dsl.render(condition)).isEqualTo("col_name between cast(? as timestamp) and cast(? as timestamp)");
    }
}