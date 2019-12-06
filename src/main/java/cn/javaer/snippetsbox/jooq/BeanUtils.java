package cn.javaer.snippetsbox.jooq;

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
class BeanUtils {
    private BeanUtils() {}

    static Pair<Optional<Configuration>, Optional<Map<String, Configuration>>> getJooqConfiguration(BeanFactory beanFactory) {
        ConfigurableListableBeanFactory listableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
        Map<String, Configuration> configurationMap = listableBeanFactory.getBeansOfType(Configuration.class);
        Configuration primaryConfiguration;
        if (configurationMap.size() == 1) {
            primaryConfiguration = configurationMap.values().iterator().next();
            return Pair.of(Optional.of(primaryConfiguration), Optional.empty());
        }
        for (Map.Entry<String, Configuration> entry : configurationMap.entrySet()) {
            BeanDefinition beanDefinition = listableBeanFactory.getBeanDefinition(entry.getKey());
            if (beanDefinition.isPrimary()) {
                primaryConfiguration = entry.getValue();
                configurationMap.remove(entry.getKey());
                return Pair.of(Optional.of(primaryConfiguration), Optional.of(configurationMap));
            }
        }
        return Pair.of(Optional.empty(), Optional.of(configurationMap));
    }
}
