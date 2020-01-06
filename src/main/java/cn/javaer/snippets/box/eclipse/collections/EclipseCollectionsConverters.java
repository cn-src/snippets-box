package cn.javaer.snippets.box.eclipse.collections;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class EclipseCollectionsConverters {

    public enum FromJavaConverter implements ConditionalGenericConverter {

        /**
         * FromJavaConverter INSTANCE.
         */
        INSTANCE {
            /**
             * (non-Javadoc)
             * @see org.springframework.core.convert.converter.GenericConverter#getConvertibleTypes()
             */
            @Override
            public Set<ConvertiblePair> getConvertibleTypes() {
                return FromJavaConverter.CONVERTIBLE_PAIRS;
            }

            /**
             * (non-Javadoc)
             * @see org.springframework.core.convert.converter.ConditionalConverter#matches(org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
             */
            @Override
            public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {

                // Prevent collections to be mapped to maps
                if (sourceType.isCollection() && MapIterable.class.isAssignableFrom(targetType.getType())) {
                    return false;
                }

                // Prevent maps to be mapped to collections
                return !sourceType.isMap() || (MapIterable.class.isAssignableFrom(targetType.getType())
                        || targetType.getType().equals(RichIterable.class));
            }

            /**
             * (non-Javadoc)
             * @see org.springframework.core.convert.converter.GenericConverter#convert(java.lang.Object, org.springframework.core.convert.TypeDescriptor, org.springframework.core.convert.TypeDescriptor)
             */
            @Nullable
            @Override
            public Object convert(@Nullable final Object source, final TypeDescriptor sourceDescriptor, final TypeDescriptor targetDescriptor) {

                final Class<?> targetType = targetDescriptor.getType();

                if (ImmutableList.class.isAssignableFrom(targetType)) {
                    return Lists.immutable.ofAll((Iterable<?>) source);
                }

                if (MutableList.class.isAssignableFrom(targetType)) {
                    return Lists.mutable.ofAll((Iterable<?>) source);
                }

                if (ImmutableSet.class.isAssignableFrom(targetType)) {
                    return Sets.immutable.ofAll((Iterable<?>) source);
                }

                if (MutableSet.class.isAssignableFrom(targetType)) {
                    return Sets.mutable.ofAll((Iterable<?>) source);
                }

                if (ImmutableMap.class.isAssignableFrom(targetType)) {
                    return Maps.immutable.ofMap((Map<?, ?>) source);
                }

                if (MutableMap.class.isAssignableFrom(targetType)) {
                    return Maps.mutable.ofMap((Map<?, ?>) source);
                }

                // No dedicated type asked for, probably Traversable.
                // Try to stay as close to the source value.

                if (source instanceof List) {
                    return Lists.immutable.ofAll((Iterable<?>) source);
                }

                if (source instanceof Set) {
                    return Sets.immutable.ofAll((Iterable<?>) source);
                }

                if (source instanceof Map) {
                    return Maps.immutable.ofAll((Map<?, ?>) source);
                }

                return source;
            }
        };

        private static final Set<GenericConverter.ConvertiblePair> CONVERTIBLE_PAIRS;

        static {

            final Set<GenericConverter.ConvertiblePair> pairs = new HashSet<>();
            pairs.add(new GenericConverter.ConvertiblePair(Collection.class, RichIterable.class));
            pairs.add(new GenericConverter.ConvertiblePair(Map.class, MapIterable.class));

            CONVERTIBLE_PAIRS = Collections.unmodifiableSet(pairs);
        }
    }
}
