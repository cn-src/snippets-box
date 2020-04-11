package cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc;

import org.jooq.JSONB;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.mapping.model.SimpleTypeHolder;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cn-src
 */
public class JooqJdbcCustomConversions extends JdbcCustomConversions {
    private SimpleTypeHolder simpleTypeHolder;

    public JooqJdbcCustomConversions(final List<?> converters) {
        super(converters);
    }

    @PostConstruct
    public void init() {
        final Set<Class<?>> simpleTypes = new HashSet<>();
        simpleTypes.add(JSONB.class);
        this.simpleTypeHolder = new SimpleTypeHolder(simpleTypes, super.getSimpleTypeHolder());
    }

    @Override
    public SimpleTypeHolder getSimpleTypeHolder() {
        return this.simpleTypeHolder;
    }

    @Override
    public boolean isSimpleType(final Class<?> type) {
        return this.simpleTypeHolder.isSimpleType(type);
    }
}
