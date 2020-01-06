package cn.javaer.snippets.box.jooq;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import org.jooq.impl.TableRecordImpl;

/**
 * @author cn-src
 */
public class JooqRecordIgnoreSupperIntrospector extends JacksonAnnotationIntrospector {
    private static final long serialVersionUID = -6633662166688748552L;

    @Override
    public boolean hasIgnoreMarker(final AnnotatedMember m) {
        return m.getDeclaringClass() == TableRecordImpl.class || super.hasIgnoreMarker(m);
    }
}