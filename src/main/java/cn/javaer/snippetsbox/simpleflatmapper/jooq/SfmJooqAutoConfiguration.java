package cn.javaer.snippetsbox.simpleflatmapper.jooq;

import org.simpleflatmapper.jooq.JooqMapperFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * SimpleFlatMapper jOOQ automatic configuration.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({JooqMapperFactory.class})
@ConditionalOnBean({org.jooq.Configuration.class})
@AutoConfigureAfter({JooqAutoConfiguration.class})
public class SfmJooqAutoConfiguration implements InitializingBean {

    private final List<org.jooq.Configuration> jooqConfigurations;

    @Autowired
    public SfmJooqAutoConfiguration(
            List<org.jooq.Configuration> jooqConfigurations) {
        this.jooqConfigurations = jooqConfigurations;
    }

    @Override
    public void afterPropertiesSet() {
        jooqConfigurations.forEach(configuration -> {
            configuration.set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordMapperProvider());
            configuration.set(JooqMapperFactory.newInstance().ignorePropertyNotFound().newRecordUnmapperProvider(configuration));
        });
    }
}
