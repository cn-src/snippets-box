package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.JSONB;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

/**
 * @author cn-src
 */
public class JsonbConverters {

    public static final ToJsonbConverter TO_JSONB_CONVERTER = ToJsonbConverter.INSTANCE;
    public static final JsonbToConverter JSONB_TO_CONVERTER = JsonbToConverter.INSTANCE;

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
