package cn.javaer.snippets.box.jooq.spring.record;

import cn.javaer.snippets.box.jooq.CurrentUserProvider;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordContext;
import org.jooq.impl.DefaultRecordListener;

import java.time.LocalDateTime;

/**
 * @author cn-src
 */
public class AuditableRecordListener extends DefaultRecordListener {

    private static final String CREATED_DATE = "created_date";
    private static final String CREATED_BY = "created_by_id";
    private static final String LAST_MODIFIED_DATE = "last_modified_date";
    private static final String LAST_MODIFIED_BY = "last_modified_by_id";

    private final CurrentUserProvider currentUserProvider;

    public AuditableRecordListener(final CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
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
                record.set((Field<Object>) field, this.currentUserProvider.currentUser());
            }
            else if (AuditableRecordListener.LAST_MODIFIED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, this.currentUserProvider.currentUser());
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
                record.set((Field<Object>) field, this.currentUserProvider.currentUser());
            }
        }
    }
}
