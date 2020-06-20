package cn.javaer.snippets.jooq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.JSONB;
import org.jooq.impl.DSL;

import java.util.Collections;

/**
 * @author cn-src
 */
public abstract class Sql {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Sql() {}

    public static Condition arrayContained(final Field<String[]> arrayField, final String[] arrayValue) {
        return DSL.condition("{0} <@ {1}", arrayField,
                DSL.val(arrayValue, arrayField.getDataType()));
    }

    public static Condition jsonbContains(final Field<JSONB> jsonField, final String jsonKey, final Object jsonValue) {
        try {
            final String json = Sql.objectMapper.writeValueAsString(Collections.singletonMap(jsonKey, jsonValue));
            return DSL.condition("{0}::jsonb @> {1}::jsonb", jsonField,
                    DSL.val(json, jsonField.getDataType()));
        }
        catch (final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Condition jsonbContains(final Field<JSONB> jsonField, final JSONB jsonb) {
        return DSL.condition("{0}::jsonb @> {1}::jsonb", jsonField,
                DSL.val(jsonb, jsonField.getDataType()));
    }
}
