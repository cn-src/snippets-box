package cn.javaer.snippets.spring.format;

import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.DateTimeFormatterFactory;
import org.springframework.format.datetime.standard.TemporalAccessorPrinter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

/**
 * @author cn-src
 */
public class DateFillFormatter extends EmbeddedValueResolutionSupport implements AnnotationFormatterFactory<DateFillFormat> {
    @Override
    public Set<Class<?>> getFieldTypes() {
        return Collections.singleton(LocalDateTime.class);
    }

    @Override
    public Printer<?> getPrinter(final DateFillFormat annotation, final Class<?> fieldType) {

        DateTimeFormatter formatter = this.getFormatter(
                annotation,
                annotation.printPattern(),
                DateTimeFormat.ISO.DATE_TIME);

        if (this.isLocal(fieldType)) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }
        return new TemporalAccessorPrinter(formatter);
    }

    @Override
    public Parser<?> getParser(final DateFillFormat annotation, final Class<?> fieldType) {
        return new DateFillParser(annotation,
                this.getFormatter(annotation, annotation.pattern(), DateTimeFormat.ISO.DATE));
    }

    public DateTimeFormatter getFormatter(final DateFillFormat annotation,
                                          final String pattern,
                                          final DateTimeFormat.ISO iso) {
        final DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        final String style = this.resolveEmbeddedValue(annotation.style());
        if (StringUtils.hasLength(style)) {
            factory.setStylePattern(style);
        }
        factory.setIso(iso);
        final String pt = this.resolveEmbeddedValue(pattern);
        if (StringUtils.hasLength(pt)) {
            factory.setPattern(pt);
        }
        return factory.createDateTimeFormatter();
    }

    private boolean isLocal(final Class<?> fieldType) {
        return fieldType.getSimpleName().startsWith("Local");
    }
}
