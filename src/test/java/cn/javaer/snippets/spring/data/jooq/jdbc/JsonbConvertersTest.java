package cn.javaer.snippets.spring.data.jooq.jdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class JsonbConvertersTest {
    @Test
    void jsonNodeToConverter() {
        final ObjectNode node = new ObjectNode(new JsonNodeFactory(true));
        node.put("k1", "v1");
        final PGobject obj = JsonbConverters.JsonNodeToConverter.INSTANCE.convert(node);
        assertThat(obj.getValue()).isEqualTo("{\"k1\":\"v1\"}");
    }

    @Test
    void toJsonNodeConverter() throws Exception {
        final ObjectNode node = new ObjectNode(new JsonNodeFactory(true));
        node.put("k1", "v1");
        final PGobject pg = new PGobject();
        pg.setValue("{\"k1\":\"v1\"}");
        final JsonNode jsonNode = JsonbConverters.ToJsonNodeConverter.INSTANCE.convert(pg);
        assertThat(Objects.requireNonNull(jsonNode).get("k1").asText()).isEqualTo("v1");
    }
}