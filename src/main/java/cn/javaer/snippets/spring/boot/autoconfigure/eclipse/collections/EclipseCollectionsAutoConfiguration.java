package cn.javaer.snippets.spring.boot.autoconfigure.eclipse.collections;

import org.eclipse.collections.api.InternalIterable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.lang.reflect.Field;

/**
 * EclipseCollections automatic configuration.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({InternalIterable.class, RepositoryFactorySupport.class})
@ConditionalOnProperty(prefix = "snippets.eclipse.collections", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class EclipseCollectionsAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        final Class<RepositoryFactorySupport> clazz = RepositoryFactorySupport.class;
        final Field conversionServiceField = clazz.getDeclaredField("CONVERSION_SERVICE");
        conversionServiceField.setAccessible(true);
        final GenericConversionService conversionService = (GenericConversionService) conversionServiceField.get(null);
        conversionService.addConverter(EclipseCollectionsConverters.FromJavaConverter.INSTANCE);
    }
}
