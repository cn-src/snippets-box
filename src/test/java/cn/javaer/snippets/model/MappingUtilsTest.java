package cn.javaer.snippets.model;

import cn.javaer.snippets.model.pojo.Product;
import cn.javaer.snippets.model.pojo.Product2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.List;

/**
 * @author cn-src
 */
class MappingUtilsTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void toOneToManyMap() throws Exception {
        final List<Product> products = Arrays.asList(
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-2", "c2-2", 2L),
                new Product(3L, "n1", "c1-1", "c2-2", 2L)
        );
        final List<Product2> results = MappingUtils.mergePropertyToMap(
                products,
                Product::getId,
                Product2::getId,
                Product2::getDynamicData,
                Product2::setDynamicData,
                p -> String.format("%s-%s", p.getCategory1(), p.getCategory2()),
                (p, old) -> p.getCount() + (old == null ? 0 : old),
                p -> new Product2(p.getId(), p.getName())

        );
        //language=JSON
        JSONAssert.assertEquals("[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"n1\",\n" +
                "    \"dynamicData\": {\n" +
                "      \"c1-1-c2-2\": 4\n" +
                "    }\n" +
                "  }, {\n" +
                "  \"id\": 2,\n" +
                "  \"name\": \"n1\",\n" +
                "  \"dynamicData\": {\n" +
                "    \"c1-1-c2-2\": 2,\n" +
                "    \"c1-2-c2-2\": 2\n" +
                "  }\n" +
                "}, {\n" +
                "  \"id\": 3,\n" +
                "  \"name\": \"n1\",\n" +
                "  \"dynamicData\": {\n" +
                "    \"c1-1-c2-2\": 2\n" +
                "  }\n" +
                "}\n" +
                "]", this.objectMapper.writeValueAsString(results), false);
    }

    @Test
    void toOneToManyMap2() throws Exception {
        final List<Product> products = Arrays.asList(
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-2", "c2-2", 2L),
                new Product(3L, "n1", "c1-1", "c2-2", 2L)
        );

        final List<Product> results = MappingUtils.mergePropertyToMap(
                products,
                Product::getId,
                Product::getDynamicData,
                Product::setDynamicData,
                p -> String.format("%s-%s", p.getCategory1(), p.getCategory2()),
                (p, old) -> p.getCount() + (old == null ? 0 : old)

        );
        //language=JSON
        JSONAssert.assertEquals("[\n" +
                "  {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"n1\",\n" +
                "    \"category1\": \"c1-1\",\n" +
                "    \"category2\": \"c2-2\",\n" +
                "    \"count\": 2,\n" +
                "    \"dynamicData\": {\n" +
                "      \"c1-1-c2-2\": 4\n" +
                "    }\n" +
                "  }, {\n" +
                "  \"id\": 2,\n" +
                "  \"name\": \"n1\",\n" +
                "  \"category1\": \"c1-1\",\n" +
                "  \"category2\": \"c2-2\",\n" +
                "  \"count\": 2,\n" +
                "  \"dynamicData\": {\n" +
                "    \"c1-1-c2-2\": 2,\n" +
                "    \"c1-2-c2-2\": 2\n" +
                "  }\n" +
                "}, {\n" +
                "  \"id\": 3,\n" +
                "  \"name\": \"n1\",\n" +
                "  \"category1\": \"c1-1\",\n" +
                "  \"category2\": \"c2-2\",\n" +
                "  \"count\": 2,\n" +
                "  \"dynamicData\": {\n" +
                "    \"c1-1-c2-2\": 2\n" +
                "  }\n" +
                "}\n" +
                "]", this.objectMapper.writeValueAsString(results), false);
    }
}