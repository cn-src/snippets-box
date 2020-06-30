package cn.javaer.snippets.jooq;

import cn.javaer.snippets.jooq.condition.ConditionCreator;
import cn.javaer.snippets.model.TreeNode;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * @author cn-src
 */
class ConditionCreatorTest {
    static {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
    }

    DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
    ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    void conditionFull() {
        final QueryFull queryFull = QueryFull.builder()
                .str1("s1")
                .str2("s2")
                .str3(new String[]{"s3-1", "s3-2"})
                .str4(new String[]{"s4-1", "s4-2"})
                .num1(1)
                .num2(2)
                .num3(3)
                .num4(4)
                .jsonb1(JSONB.valueOf("{}"))
                .startDate(LocalDate.of(2020, 1, 11))
                .endDate(LocalDate.of(2020, 3, 11))
                .startNum(23)
                .endNum(50)
                .build();

        final Condition condition = ConditionCreator.create(queryFull);
        assertThat(this.dsl.render(condition)).isEqualTo("((jsonb1::jsonb @> cast(? as jsonb)::jsonb) and num1 < ? and num2 <= ? and num3 > ? and num4 >= ? and str1 = ? and cast(str2 as varchar) like ('%' || replace(replace(replace(?, '!', '!!'), '%', '!%'), '_', '!_') || '%') escape '!' and str3 @> ?::varchar[] and (str4 <@ ?::varchar[]) and col_num between ? and ? and col_date between cast(? as timestamp) and cast(? as timestamp))");
    }

    @Test
    void exceptionQuery() {
        assertThatIllegalStateException()
                .isThrownBy(() -> ConditionCreator.create(new ExceptionQuery("s1")));
    }

    @Test
    void treeNodeListToCondition() throws Exception {

        //language=JSON
        final List<TreeNode> treeNodes = this.objectMapper.readValue("[\n" +
                "  {\n" +
                "    \"title\": \"1\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"1-1\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"title\": \"1-1-1\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"1-2\",\n" +
                "        \"children\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"title\": \"2\",\n" +
                "    \"children\": [\n" +
                "      {\n" +
                "        \"title\": \"2-1\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"title\": \"2-1-1\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"2-2-1\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]\n", new TypeReference<List<TreeNode>>() {});
        final Condition condition = ConditionCreator.create(treeNodes,
                DSL.field("f1", String.class),
                DSL.field("f2", String.class),
                DSL.field("f3", String.class));
        System.out.println(this.dsl.renderInlined(condition));
    }
}