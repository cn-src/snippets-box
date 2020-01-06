package cn.javaer.snippets.box.jooq.spring.record;

import org.jooq.Configuration;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.impl.TableRecordImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.TypeUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cn-src
 */
public class JooqRecordClassConverter<T extends ConversionService & ConverterRegistry>
        implements ConditionalGenericConverter {

    private final T conversionService;
    private ToEntityConverter toEntityConverter;
    private ToIdConverter toIdConverter;
    private static final Map<Class<?>, Class<?>> CACHE = new ConcurrentHashMap<>();

    public JooqRecordClassConverter(final T conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null!");

        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }

    @Nullable
    @Override
    public Object convert(@Nullable final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        final ConditionalGenericConverter converter = this.getConverter(targetType);
        return converter.convert(source, sourceType, targetType);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
     */
    @Override
    public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
        final ConditionalGenericConverter converter = this.getConverter(targetType);
        return converter.matches(sourceType, targetType);
    }

    private ConditionalGenericConverter getConverter(final TypeDescriptor targetType) {
        return TypeUtils.isAssignable(TableRecordImpl.class, targetType.getType()) ? this.toEntityConverter : this.toIdConverter;
    }

    public void setConfiguration(final Configuration configuration) {

        this.toEntityConverter = new ToEntityConverter(configuration);
        this.conversionService.addConverter(this.toEntityConverter);

        this.toIdConverter = new ToIdConverter();
        this.conversionService.addConverter(this.toIdConverter);
    }

    private class ToEntityConverter implements ConditionalGenericConverter {

        private final Configuration configuration;

        ToEntityConverter(final Configuration configuration) {
            this.configuration = configuration;
        }

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
         */
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
        }

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
         */
        @Nullable
        @Override
        public Object convert(@Nullable final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {

            if (source == null || !StringUtils.hasText(source.toString())) {
                return null;
            }

            if (sourceType.equals(targetType)) {
                return source;
            }

            final Class<?> recordType = targetType.getType();
            final UpdatableRecord<?> updatableRecord = JooqRecordClassConverter.this.newUpdatableRecord(recordType);

            final Class<?> rawIdType = JooqRecordClassConverter.getIdField(updatableRecord).getType();

            final Object id = JooqRecordClassConverter.this.conversionService.convert(source, rawIdType);
            if (id == null) {
                return null;
            }
            updatableRecord.attach(this.configuration);
            updatableRecord.set(JooqRecordClassConverter.getIdField(updatableRecord), id);
            return updatableRecord;
        }

        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {

            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }

            final Class<?> recordType = targetType.getType();

            if (targetType.isAssignableTo(TypeDescriptor.valueOf(UpdatableRecord.class))) {

                final Class<?> rawIdType = JooqRecordClassConverter.this.getIdClass(recordType);
                return sourceType.equals(TypeDescriptor.valueOf(rawIdType)) ||
                        JooqRecordClassConverter.this.conversionService.canConvert(sourceType.getType(), rawIdType);
            }
            return false;
        }
    }

    class ToIdConverter implements ConditionalGenericConverter {

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
         */
        @Override
        public Set<ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
        }

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
         */
        @Nullable
        @Override
        public Object convert(@Nullable final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {

            if (source == null || !StringUtils.hasText(source.toString())) {
                return null;
            }

            if (sourceType.equals(targetType)) {
                return source;
            }

            final UpdatableRecord<?> updatableRecord = (UpdatableRecord<?>) source;

            return JooqRecordClassConverter.this.conversionService.convert(updatableRecord.get(JooqRecordClassConverter.getIdField(updatableRecord)), targetType.getType());
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
         */
        @Override
        public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {

            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }

            final Class<?> recordType = sourceType.getType();

            if (!TypeUtils.isAssignable(UpdatableRecord.class, recordType)) {
                return false;
            }

            final Class<?> rawIdType = JooqRecordClassConverter.this.getIdClass(recordType);

            return targetType.equals(TypeDescriptor.valueOf(rawIdType)) ||
                    JooqRecordClassConverter.this.conversionService.canConvert(rawIdType, targetType.getType());
        }
    }

    private UpdatableRecord<?> newUpdatableRecord(final Class<?> recordType) {
        try {
            return (UpdatableRecord<?>) recordType.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <R extends UpdatableRecord<R>> TableField<R, ?> getIdField(final UpdatableRecord<R> updatableRecord) {
        return updatableRecord.getTable().getPrimaryKey().getFieldsArray()[0];
    }

    private Class<?> getIdClass(final Class<?> recordType) {
        return JooqRecordClassConverter.CACHE.computeIfAbsent(recordType, aClass -> this.newUpdatableRecord(aClass).getTable().getPrimaryKey().getFieldsArray()[0].getType());
    }
}
