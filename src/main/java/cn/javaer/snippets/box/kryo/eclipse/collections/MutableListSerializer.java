package cn.javaer.snippets.box.kryo.eclipse.collections;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

/**
 * A kryo {@link Serializer} for eclipse-collections {@link MutableList}.
 *
 * @author cn-src
 */
public class MutableListSerializer extends Serializer<MutableList<Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;
    private static final boolean IMMUTABLE = false;

    public MutableListSerializer() {
        super(MutableListSerializer.DOES_NOT_ACCEPT_NULL, MutableListSerializer.IMMUTABLE);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final MutableList<Object> object) {
        output.writeInt(object.size(), true);
        for (final Object elm : object) {
            kryo.writeClassAndObject(output, elm);
        }
    }

    @Override
    public MutableList<Object> read(final Kryo kryo, final Input input, final Class<? extends MutableList<Object>> type) {
        final int size = input.readInt(true);
        final Object[] list = new Object[size];
        for (int i = 0; i < size; ++i) {
            list[i] = kryo.readClassAndObject(input);
        }
        return Lists.mutable.of(list);
    }

    @Override
    public MutableList<Object> copy(final Kryo kryo, final MutableList<Object> original) {
        return original.clone();
    }

    /**
     * Creates a new {@link MutableListSerializer} and registers its serializer
     * for the several ImmutableList related classes.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on.
     */
    public static void registerSerializers(final Kryo kryo) {

        final MutableListSerializer serializer = new MutableListSerializer();

        kryo.register(MutableList.class, serializer);

        RegisterUtil.register(kryo, serializer, MutableList.class,
                "org.eclipse.collections.impl.list.mutable",
                "org.eclipse.collections.impl.list.fixed");
    }
}
