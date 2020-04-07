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
        final Field<String> objectField = DSL.field("object", String.class);
        final Field<String[]> arrayField = DSL.field("array", String[].class);
        new ConditionBuilder()
                .append(objectField::contains, "")
                .append(arrayField::contains, "", "");
    }
}