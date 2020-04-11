package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.JSONB;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

/**
 * @author cn-src
 */
@ReadingConverter
public enum JsonbConverter implements Converter<PGobject, JSONB> {

    /**
     * 单实例.
     */
    INSTANCE;

    @Override
    public JSONB convert(final PGobject source) {
        return JSONB.valueOf(source.getValue());
    }
}
