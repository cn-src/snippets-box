package cn.javaer.snippets.kryo.serializers.eclipse.collections;

import cn.javaer.snippets.kryo.serializers.RegisterUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * A kryo {@link Serializer} for eclipse-collections {@link ImmutableList}.
 *
 * @author cn-src
 */
public class ImmutableListSerializer extends Serializer<ImmutableList<Object>> {

    private static final boolean DOES_NOT_ACCEPT_NULL = false;
    private static final boolean IMMUTABLE = true;

    public ImmutableListSerializer() {
        super(ImmutableListSerializer.DOES_NOT_ACCEPT_NULL, ImmutableListSerializer.IMMUTABLE);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final ImmutableList<Object> object) {
        output.writeInt(object.size(), true);
        for (final Object elm : object) {
            kryo.writeClassAndObject(output, elm);
        }
    }

    @Override
    public ImmutableList<Object> read(final Kryo kryo, final Input input, final Class<? extends ImmutableList<Object>> type) {
        final int size = input.readInt(true);
        final Object[] list = new Object[size];
        for (int i = 0; i < size; ++i) {
            list[i] = kryo.readClassAndObject(input);
        }
        return Lists.immutable.of(list);
    }

    /**
     * Creates a new {@link ImmutableListSerializer} and registers its serializer
     * for the several ImmutableList related classes.
     *
     * @param kryo the {@link Kryo} instance to set the serializer on.
     */
    public static void registerSerializers(final Kryo kryo) {

        final ImmutableListSerializer serializer = new ImmutableListSerializer();

        kryo.register(ImmutableList.class, serializer);

        RegisterUtil.register(kryo, serializer, ImmutableList.class,
                "org.eclipse.collections.impl.list.immutable");
    }
}
