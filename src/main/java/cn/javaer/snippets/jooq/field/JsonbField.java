package cn.javaer.snippets.jooq.field;

import org.jooq.Condition;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.CustomField;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONValue;

import java.util.Collections;

/**
 * @author cn-src
 */
public class JsonbField<R extends Record, T> extends CustomField<T> implements TableField<R, T> {
    private static final long serialVersionUID = -2128410511798819756L;
    private final Table<R> table;

    public JsonbField(final String name, final DataType<T> type, final Table<R> table) {
        super(name, type);

        this.table = table;
    }

    @Override
    public void accept(final Context ctx) {
        if (ctx.qualify()) {
            ctx.visit(this.table);
            ctx.sql('.');
        }
        ctx.visit(this.getUnqualifiedName());
    }

    @SuppressWarnings("unchecked")
    @Support(SQLDialect.POSTGRES)
    public Condition containsJson(final T jsonObj) {
        if (jsonObj instanceof Field) {
            return this.containsJson((Field) jsonObj);
        }
        return DSL.condition("{0}::jsonb @> {1}::jsonb", this,
                DSL.val(jsonObj, this.getDataType()));
    }

    @Support(SQLDialect.POSTGRES)
    public Condition containsJson(final Field<T> jsonField) {
        return DSL.condition("{0}::jsonb @> {1}::jsonb", this,
                jsonField);
    }

    @Support(SQLDialect.POSTGRES)
    public Condition containsJson(final String jsonKey, final Object jsonValue) {
        final String json = JSONValue.toJSONString(Collections.singletonMap(jsonKey, jsonValue));
        return DSL.condition("{0}::jsonb @> {1}::jsonb", this,
                DSL.val(json, this.getDataType()));
    }

    @Override
    public Table<R> getTable() {
        return this.table;
    }
}
