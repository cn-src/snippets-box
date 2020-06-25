package cn.javaer.snippets.jooq.codegen;

import org.jooq.codegen.GeneratorWriter;
import org.jooq.codegen.JavaGenerator;
import org.jooq.codegen.JavaWriter;
import org.jooq.meta.SchemaDefinition;
import org.jooq.meta.TableDefinition;

import java.lang.reflect.Field;

/**
 * @author cn-src
 */
public class SnippetsGenerator extends JavaGenerator {
    @Override
    protected void generateTable(final SchemaDefinition schema, final TableDefinition table) {
        final JavaWriter out = this.newJavaWriter(this.getFile(table));
        this.generateTable(table, out);
        try {
            final Field field = GeneratorWriter.class.getDeclaredField("sb");
            field.setAccessible(true);
            final StringBuilder sb = (StringBuilder) field.get(out);
            final String newSb = sb.toString().replaceAll("public final TableField<Record, JSONB>(.*)createField\\(DSL.name\\((.*)\\), org.jooq.impl.SQLDataType.JSONB, this, (.*)\\);",
                    "public final cn.javaer.snippets.jooq.field.JsonbField<Record, JSONB>$1new cn.javaer.snippets.jooq.field.JsonbField($2, org.jooq.impl.SQLDataType.JSONB, this);");
            sb.setLength(0);
            sb.append(newSb);
        }
        catch (final IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
        this.closeJavaWriter(out);
    }
}
