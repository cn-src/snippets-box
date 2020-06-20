package cn.javaer.snippets.spring.boot.autoconfigure.jackson;

import cn.javaer.snippets.jackson.SnippetsJacksonIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
public class SnippetsJacksonAutoConfiguration implements InitializingBean {
    private final ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public SnippetsJacksonAutoConfiguration(@Autowired(required = false) final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null != this.objectMapper) {
            this.objectMapper.setAnnotationIntrospector(SnippetsJacksonIntrospector.INSTANCE);
        }
    }
}
