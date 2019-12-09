package cn.javaer.snippetsbox.jooq;

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

    public AuditableRecordListener(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void insertStart(RecordContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        Record record = ctx.record();
        for (Field<?> field : record.fields()) {
            String fieldName = field.getName();
            if (AuditableRecordListener.CREATED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, now);
            }
            else if (AuditableRecordListener.LAST_MODIFIED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, now);
            }
            else if (AuditableRecordListener.CREATED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, currentUserProvider.currentUser());
            }
            else if (AuditableRecordListener.LAST_MODIFIED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, currentUserProvider.currentUser());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateStart(RecordContext ctx) {
        Record record = ctx.record();
        for (Field<?> field : record.fields()) {
            String fieldName = field.getName();
            if (AuditableRecordListener.LAST_MODIFIED_DATE.equalsIgnoreCase(fieldName)) {
                record.set((Field<LocalDateTime>) field, LocalDateTime.now());
            }
            else if (AuditableRecordListener.LAST_MODIFIED_BY.equalsIgnoreCase(fieldName)) {
                record.set((Field<Object>) field, currentUserProvider.currentUser());
            }
        }
    }
}
