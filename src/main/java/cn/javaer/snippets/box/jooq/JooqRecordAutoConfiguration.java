package cn.javaer.snippets.box.jooq;

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
import org.springframework.util.Assert;

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
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") final
            List<ObjectMapper> objectMappers) {
        Assert.notEmpty(objectMappers, () -> "ObjectMapper must be required");

        this.objectMappers = objectMappers;
    }

    @Bean
    public JooqRecordAttachAdvice attachAdvice() {
        return new JooqRecordAttachAdvice();
    }

    @Override
    public void afterPropertiesSet() {
        this.objectMappers.forEach(objectMapper ->
                objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector()));
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(name = "mvcConversionService")
    @AutoConfigureAfter({JooqAutoConfiguration.class, WebMvcAutoConfiguration.class})
    static class JooqRecordWebConfiguration implements InitializingBean {
        private final BeanFactory beanFactory;
        private final FormattingConversionService conversionService;

        public JooqRecordWebConfiguration(
                final BeanFactory beanFactory,
                @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
                @Qualifier("mvcConversionService") final FormattingConversionService conversionService) {
            this.beanFactory = beanFactory;
            this.conversionService = conversionService;
        }

        @Override
        public void afterPropertiesSet() {
            final org.jooq.Configuration configuration = BeanUtils.getJooqConfiguration(this.beanFactory).getFirst().orElseThrow(IllegalStateException::new);
            final JooqRecordClassConverter<FormattingConversionService> jooqRecordClassConverter = new JooqRecordClassConverter<>(this.conversionService);
            jooqRecordClassConverter.setConfiguration(configuration);
        }
    }
}
