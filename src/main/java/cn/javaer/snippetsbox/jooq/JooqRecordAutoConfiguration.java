package cn.javaer.snippetsbox.jooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;

import javax.sql.DataSource;
import java.util.List;

/**
 * jOOQ record type support.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DSLContext.class)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({JooqAutoConfiguration.class, JacksonAutoConfiguration.class})
public class JooqRecordAutoConfiguration implements InitializingBean {

    private final List<ObjectMapper> objectMappers;

    @Autowired
    public JooqRecordAutoConfiguration(
            List<ObjectMapper> objectMappers) {
        this.objectMappers = objectMappers;
    }

    @Bean
    public JooqRecordAttachAdvice attachAdvice() {
        return new JooqRecordAttachAdvice();
    }

    @Override
    public void afterPropertiesSet() {
        objectMappers.forEach(objectMapper ->
                objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector()));
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(name = "mvcConversionService")
    @AutoConfigureAfter({JooqAutoConfiguration.class, WebMvcAutoConfiguration.class})
    static class JooqRecordWebConfiguration implements InitializingBean {
        private final BeanFactory beanFactory;
        private final FormattingConversionService conversionService;

        public JooqRecordWebConfiguration(
                BeanFactory beanFactory,
                @Qualifier("mvcConversionService") FormattingConversionService conversionService) {
            this.beanFactory = beanFactory;
            this.conversionService = conversionService;
        }

        @Override
        public void afterPropertiesSet() {
            org.jooq.Configuration configuration = BeanUtils.getJooqConfiguration(beanFactory).getFirst().orElseThrow(IllegalStateException::new);
            JooqRecordClassConverter<FormattingConversionService> jooqRecordClassConverter = new JooqRecordClassConverter<>(conversionService);
            jooqRecordClassConverter.setConfiguration(configuration);
        }
    }
}
