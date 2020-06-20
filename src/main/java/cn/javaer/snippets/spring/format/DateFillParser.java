package cn.javaer.snippets.spring.format;

import org.springframework.format.Parser;
import org.springframework.format.datetime.standard.DateTimeContextHolder;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author cn-src
 */
public final class DateFillParser implements Parser<LocalDateTime> {
    private final DateFillFormat annotation;
    private final DateTimeFormatter formatter;

    public DateFillParser(final DateFillFormat annotation, final DateTimeFormatter formatter) {
        this.annotation = annotation;
        this.formatter = formatter;
    }

    @Override
    public LocalDateTime parse(final String text, final Locale locale) throws ParseException {
        final DateTimeFormatter formatterToUse = DateTimeContextHolder.getFormatter(this.formatter, locale);

        final LocalDate date = LocalDate.parse(text, formatterToUse);
        switch (this.annotation.fillTime()) {
            case MIN:
                return date.atTime(LocalTime.MIN);
            case MAX:
                return date.atTime(LocalTime.MAX);
            default:
                throw new IllegalStateException();
        }
    }
}
