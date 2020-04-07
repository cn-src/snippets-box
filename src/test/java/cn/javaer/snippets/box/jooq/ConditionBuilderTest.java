package cn.javaer.snippets.box.jooq;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

/**
 * @author cn-src
 */
class ConditionBuilderTest {

    @Test
    void append() {
        final Field<String[]> nameField = DSL.field("name", String[].class);
        new ConditionBuilder()
                .appendWithArray(nameField::contains, "");
    }
}