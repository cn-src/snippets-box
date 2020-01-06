package cn.javaer.snippets.box.jooq.spring.record;

import org.jooq.Configuration;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.util.Pair;

import java.util.Map;
import java.util.Optional;

/**
 * @author cn-src
 */
public class BeanUtils {
    private BeanUtils() {}

    public static Pair<Optional<Configuration>, Optional<Map<String, Configuration>>> getJooqConfiguration(final BeanFactory beanFactory) {
        final ConfigurableListableBeanFactory listableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
        final Map<String, Configuration> configurationMap = listableBeanFactory.getBeansOfType(Configuration.class);
        final Configuration primaryConfiguration;
        if (configurationMap.size() == 1) {
            primaryConfiguration = configurationMap.values().iterator().next();
            return Pair.of(Optional.of(primaryConfiguration), Optional.empty());
        }
        for (final Map.Entry<String, Configuration> entry : configurationMap.entrySet()) {
            final BeanDefinition beanDefinition = listableBeanFactory.getBeanDefinition(entry.getKey());
            if (beanDefinition.isPrimary()) {
                primaryConfiguration = entry.getValue();
                configurationMap.remove(entry.getKey());
                return Pair.of(Optional.of(primaryConfiguration), Optional.of(configurationMap));
            }
        }
        return Pair.of(Optional.empty(), Optional.of(configurationMap));
    }
}
