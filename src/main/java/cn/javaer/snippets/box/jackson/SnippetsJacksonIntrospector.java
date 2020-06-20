package cn.javaer.snippets.box.jackson;

import cn.javaer.snippets.box.spring.format.DateFillFormat;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * @author cn-src
 */
public class SnippetsJacksonIntrospector extends JacksonAnnotationIntrospector {
    private static final long serialVersionUID = -6156647757687961666L;

    @Override
    public Object findDeserializer(final Annotated a) {
        final DateFillFormat fillFormat = this._findAnnotation(a, DateFillFormat.class);
        if (null != fillFormat) {
            return new DateFillDeserializer(fillFormat);
        }
        return super.findDeserializer(a);
    }
}
