package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.JSONB;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cn-src
 */
public abstract class JsonbConverters {
    private static final List<Converter<?, ?>> CONVERTERS = new ArrayList<>();

    static {
        JsonbConverters.CONVERTERS.add(ToJsonbConverter.INSTANCE);
        JsonbConverters.CONVERTERS.add(JsonbToConverter.INSTANCE);
    }

    public static List<Converter<?, ?>> getConvertersToRegister() {
        return Collections.unmodifiableList(JsonbConverters.CONVERTERS);
    }

    @ReadingConverter
    private enum ToJsonbConverter implements Converter<PGobject, JSONB> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public JSONB convert(final PGobject source) {
            return JSONB.valueOf(source.getValue());
        }
    }

    @WritingConverter
    private enum JsonbToConverter implements Converter<JSONB, String> {

        /**
         * 单实例.
         */
        INSTANCE;

        @Override
        public String convert(final JSONB source) {
            return source.data();
        }
    }
}
