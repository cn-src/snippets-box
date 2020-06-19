package cn.javaer.snippets.box.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author cn-src
 */
class MappingUtilsTest {

    @Test
    void toOneToManyMap() {
        final List<Product> products = Arrays.asList(
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-2", "c2-2", 2L),
                new Product(3L, "n1", "c1-1", "c2-2", 2L)
        );
        final List<Product2> r1 = MappingUtils.mergePropertyToMap(
                products,
                Product::getId,
                Product2::getId,
                Product2::getDynamicData,
                Product2::setDynamicData,
                p -> String.format("%s-%s", p.category1, p.category2),
                (p, old) -> p.count + (old == null ? 0 : old),
                p -> new Product2(p.getId(), p.getName())

        );

        System.out.println(r1);
    }

    @Test
    void toOneToManyMap2() {
        final List<Product> products = Arrays.asList(
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(1L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-1", "c2-2", 2L),
                new Product(2L, "n1", "c1-2", "c2-2", 2L),
                new Product(3L, "n1", "c1-1", "c2-2", 2L)
        );

        final List<Product> r2 = MappingUtils.mergePropertyToMap(
                products,
                Product::getId,
                Product::getDynamicData,
                Product::setDynamicData,
                p -> String.format("%s-%s", p.category1, p.category2),
                (p, old) -> p.count + (old == null ? 0 : old)

        );
        System.out.println(r2);
    }
}