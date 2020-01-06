package cn.javaer.snippets.box.spring.jooq;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.impl.DefaultRecordListener;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;

/**
 * @author cn-src
 */
public class AuditableRecordListener extends DefaultRecordListener {

    private static final String CREATED_DATE = "created_date";
    private static final String CREATED_BY = "created_by_id";
    private static final String LAST_MODIFIED_DATE = "last_modified_date";
    private static final String LAST_MODIFIED_BY = "last_modified_by_id";

    private final AuditorAware<?> auditorAware;

    public AuditableRecordListener(final AuditorAware<?> auditorAware) {
        this.auditorAware = auditorAware;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insertStart(final RecordContext ctx) {
        final LocalDateTime now = LocalDateTime.now();
        final Record record = ctx.record();
        for (final Field<?> field : record.fields()) {
            final String fieldName = field.getName();
            if (AuditableRecordListener.CREATED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, now);
            }
            else if (AuditableRecordListener.LAST_MODIFIED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, now);
            }
            else if (AuditableRecordListener.CREATED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, this.auditorAware.getCurrentAuditor());
            }
            else if (AuditableRecordListener.LAST_MODIFIED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, this.auditorAware.getCurrentAuditor());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateStart(final RecordContext ctx) {
        final Record record = ctx.record();
        for (final Field<?> field : record.fields()) {
            final String fieldName = field.getName();
            if (AuditableRecordListener.LAST_MODIFIED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, LocalDateTime.now());
            }
            else if (AuditableRecordListener.LAST_MODIFIED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, this.auditorAware.getCurrentAuditor());
            }
        }
    }
}
