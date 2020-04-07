package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author cn-src
 */
class ConditionBuilderTest {

    @Test
    void append() {
        final Field<String> objectField = DSL.field("object", String.class);
        final Field<String[]> arrayField = DSL.field("array", String[].class);
        final Field<JSONB> jsonbField = DSL.field("jsonb", JSONB.class);
        final Field<LocalDateTime> dateTimeField = DSL.field("dateTime", LocalDateTime.class);

        final Condition condition = new ConditionBuilder()
                .append(objectField::contains, "object")
                .append(arrayField::contains, "str1", "str2")
                .dateTime(dateTimeField::betweenSymmetric, LocalDate.now(), LocalDate.now())
                .append(Sql::arrayContained, arrayField, new String[]{"value"})
                .append(Sql::jsonbContains, jsonbField, "key", "value")
                .build();

        final String sql = DSL.using(SQLDialect.POSTGRES)
                .select(objectField, arrayField, jsonbField)
                .from(DSL.table("demo_table"))
                .where(condition)
                .getSQL();
        System.out.println(sql);
    }
}