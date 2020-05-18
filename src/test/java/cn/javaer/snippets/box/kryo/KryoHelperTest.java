package cn.javaer.snippets.box.kryo;

import cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections.city.City;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class KryoHelperTest {
    @Test
    void name() {
        final KryoHelper kryoHelper = new KryoHelper(kryo -> {
            kryo.register(City.class);
        });

        final byte[] bytes = kryoHelper.writeClassAndObject(new City("n1"));
        final Object city = kryoHelper.readClassAndObject(bytes);
        assertThat(city).hasFieldOrPropertyWithValue("name", "n1");
    }
}