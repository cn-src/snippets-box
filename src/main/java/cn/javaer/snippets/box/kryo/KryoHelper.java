package cn.javaer.snippets.box.kryo;

import cn.javaer.snippets.box.kryo.serializers.eclipse.collections.ImmutableListSerializer;
import cn.javaer.snippets.box.kryo.serializers.eclipse.collections.MutableListSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.Pool;
import com.esotericsoftware.kryo.util.Util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author cn-src
 */
public class KryoHelper {

    private final Consumer<Kryo> configurer;

    public KryoHelper() {
        this.configurer = kryo -> { };
    }

    public KryoHelper(final Consumer<Kryo> configurer) {
        this.configurer = configurer;
    }

    private final Pool<Input> inputPool = new Pool<Input>(true, false, 16) {
        @Override
        protected Input create() {
            return new Input();
        }
    };

    private final Pool<Output> outputPool = new Pool<Output>(true, false, 16) {
        @Override
        protected Output create() {
            return new Output(1024, -1);
        }
    };

    private final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
        @Override
        protected Kryo create() {
            final Kryo kryo = new Kryo();
            if (Util.isClassAvailable("org.eclipse.collections.api.list.ImmutableList")) {
                ImmutableListSerializer.registerSerializers(kryo);
            }
            if (Util.isClassAvailable("org.eclipse.collections.api.list.MutableList")) {
                MutableListSerializer.registerSerializers(kryo);
            }
            KryoHelper.this.configurer.accept(kryo);
            return kryo;
        }
    };

    public <T> T readObject(final byte[] bytes, final Class<T> clazz) {
        return this.read(bytes, (kryo, input) -> kryo.readObject(input, clazz));
    }

    public <T> T readObject(final byte[] bytes, final Class<T> clazz, final Serializer<?> serializer) {
        return this.read(bytes, (kryo, input) -> kryo.readObject(input, clazz, serializer));
    }

    public <T> T readObjectOrNull(final byte[] bytes, final Class<T> clazz) {
        return this.read(bytes, (kryo, input) -> kryo.readObjectOrNull(input, clazz));
    }

    public <T> T readObjectOrNull(final byte[] bytes, final Class<T> clazz, final Serializer<?> serializer) {
        return this.read(bytes, (kryo, input) -> kryo.readObjectOrNull(input, clazz, serializer));
    }

    public <T> T readClassAndObject(final byte[] bytes) {
        //noinspection unchecked
        return this.read(bytes, (kryo, input) -> (T) kryo.readClassAndObject(input));
    }

    public byte[] writeObject(final Object object) {
        return this.write((kryo, output) -> kryo.writeObject(output, object));
    }

    public byte[] writeObject(final Object object, final Serializer<?> serializer) {
        return this.write((kryo, output) -> kryo.writeObject(output, object, serializer));
    }

    public byte[] writeObjectOrNull(final Object object, final Class<?> clazz) {
        return this.write((kryo, output) -> kryo.writeObjectOrNull(output, object, clazz));
    }

    public byte[] writeObjectOrNull(final Object object, final Serializer<?> serializer) {
        return this.write((kryo, output) -> kryo.writeObjectOrNull(output, object, serializer));
    }

    public byte[] writeClassAndObject(final Object object) {
        return this.write((kryo, output) -> kryo.writeClassAndObject(output, object));
    }

    private <T> T read(final byte[] bytes, final BiFunction<Kryo, Input, T> fun) {
        Input input = null;
        Kryo kryo = null;
        try {
            input = this.inputPool.obtain();
            input.setBuffer(bytes);
            kryo = this.kryoPool.obtain();
            return fun.apply(kryo, input);
        }
        finally {
            if (null != kryo) {
                this.kryoPool.free(kryo);
            }
            if (null != input) {
                this.inputPool.free(input);
            }
        }
    }

    private byte[] write(final BiConsumer<Kryo, Output> fun) {
        Output output = null;
        Kryo kryo = null;
        try {
            output = this.outputPool.obtain();
            kryo = this.kryoPool.obtain();
            fun.accept(kryo, output);
            return output.getBuffer();
        }
        finally {
            if (null != kryo) {
                this.kryoPool.free(kryo);
            }
            if (null != output) {
                this.outputPool.free(output);
            }
        }
    }
}
