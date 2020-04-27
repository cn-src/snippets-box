package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class ConditionCreatorTest {

    @Test
    void of() {
        final Condition condition = ConditionCreator.createWithIgnoreUnannotated(new Query("demo", "demo",
                JSONB.valueOf("{\"k\":\"v\"}"),
                JSONB.valueOf("{\"k\":\"v\"}")));

        assertThat(condition.toString()).isEqualTo("(\n" +
                "  (jsonb2::jsonb @> '{\"k\":\"v\"}'::jsonb)\n" +
                "  and str2 like ('%' || replace(\n" +
                "    replace(\n" +
                "      replace(\n" +
                "        'demo', \n" +
                "        '!', \n" +
                "        '!!'\n" +
                "      ), \n" +
                "      '%', \n" +
                "      '!%'\n" +
                "    ), \n" +
                "    '_', \n" +
                "    '!_'\n" +
                "  ) || '%') escape '!'\n" +
                ")");
    }
}