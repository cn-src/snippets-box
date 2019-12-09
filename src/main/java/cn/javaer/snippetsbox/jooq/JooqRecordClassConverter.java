package cn.javaer.snippetsbox.jooq;

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

    JooqRecordClassConverter(T conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null!");

        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(Object.class, Object.class));
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConditionalGenericConverter converter = getConverter(targetType);
        return converter.convert(source, sourceType, targetType);
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
     */
    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        ConditionalGenericConverter converter = getConverter(targetType);
        return converter.matches(sourceType, targetType);
    }

    private ConditionalGenericConverter getConverter(TypeDescriptor targetType) {
        return TypeUtils.isAssignable(TableRecordImpl.class, targetType.getType()) ? toEntityConverter : toIdConverter;
    }

    public void setConfiguration(Configuration configuration) {

        toEntityConverter = new ToEntityConverter(configuration);
        conversionService.addConverter(toEntityConverter);

        toIdConverter = new ToIdConverter();
        conversionService.addConverter(toIdConverter);
    }

    private class ToEntityConverter implements ConditionalGenericConverter {

        private final Configuration configuration;

        ToEntityConverter(Configuration configuration) {
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
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

            if (source == null || !StringUtils.hasText(source.toString())) {
                return null;
            }

            if (sourceType.equals(targetType)) {
                return source;
            }

            Class<?> recordType = targetType.getType();
            UpdatableRecord<?> updatableRecord = newUpdatableRecord(recordType);

            Class<?> rawIdType = JooqRecordClassConverter.getIdField(updatableRecord).getType();

            Object id = conversionService.convert(source, rawIdType);
            if (id == null) {
                return null;
            }
            updatableRecord.attach(configuration);
            updatableRecord.set(JooqRecordClassConverter.getIdField(updatableRecord), id);
            return updatableRecord;
        }

        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }

            Class<?> recordType = targetType.getType();

            if (targetType.isAssignableTo(TypeDescriptor.valueOf(UpdatableRecord.class))) {

                Class<?> rawIdType = getIdClass(recordType);
                return sourceType.equals(TypeDescriptor.valueOf(rawIdType)) ||
                        conversionService.canConvert(sourceType.getType(), rawIdType);
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
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

            if (source == null || !StringUtils.hasText(source.toString())) {
                return null;
            }

            if (sourceType.equals(targetType)) {
                return source;
            }

            UpdatableRecord<?> updatableRecord = (UpdatableRecord<?>) source;

            return conversionService.convert(updatableRecord.get(JooqRecordClassConverter.getIdField(updatableRecord)), targetType.getType());
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
         */
        @Override
        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {

            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }

            Class<?> recordType = sourceType.getType();

            if (!TypeUtils.isAssignable(UpdatableRecord.class, recordType)) {
                return false;
            }

            Class<?> rawIdType = getIdClass(recordType);

            return targetType.equals(TypeDescriptor.valueOf(rawIdType)) ||
                    conversionService.canConvert(rawIdType, targetType.getType());
        }
    }

    private UpdatableRecord<?> newUpdatableRecord(Class<?> recordType) {
        try {
            return (UpdatableRecord<?>) recordType.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <R extends UpdatableRecord<R>> TableField<R, ?> getIdField(UpdatableRecord<R> updatableRecord) {
        return updatableRecord.getTable().getPrimaryKey().getFieldsArray()[0];
    }

    private Class<?> getIdClass(Class<?> recordType) {
        return JooqRecordClassConverter.CACHE.computeIfAbsent(recordType, aClass -> newUpdatableRecord(aClass).getTable().getPrimaryKey().getFieldsArray()[0].getType());
    }
}
