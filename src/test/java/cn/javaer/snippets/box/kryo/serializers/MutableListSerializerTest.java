package cn.javaer.snippets.box.kryo.serializers;

import cn.javaer.snippets.box.kryo.KryoHelper;
import lombok.Data;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class MutableListSerializerTest {
    private final KryoHelper kryoHelper = new KryoHelper(kryo -> {
        kryo.register(User.class);
    });

    @Test
    void serializer() throws Exception {
        final User user = new User(Lists.mutable.of("name1"));
        final byte[] bytes = this.kryoHelper.writeClassAndObject(user);
        final User userWithRead = this.kryoHelper.readClassAndObject(bytes);
        assertThat(userWithRead.names).hasSize(1);
        assertThat(userWithRead.names.get(0)).isEqualTo("name1");
    }

    @Data
    private static class User {
        MutableList<String> names;

        public User() {
        }

        public User(final MutableList<String> names) {
            this.names = names;
        }
    }
}