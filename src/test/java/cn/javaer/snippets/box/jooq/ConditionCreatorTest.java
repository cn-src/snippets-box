package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;

/**
 * @author cn-src
 */
class ConditionCreatorTest {

    @Test
    void of() {
        final Condition condition = ConditionCreator.create(new Query("demo", "demo",
                JSONB.valueOf("{\"k\":\"v\"}"),
                JSONB.valueOf("{\"k\":\"v\"}")));
        System.out.println(condition);
    }
}