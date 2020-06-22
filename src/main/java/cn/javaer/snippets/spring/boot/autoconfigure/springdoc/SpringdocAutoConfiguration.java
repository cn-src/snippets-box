package cn.javaer.snippets.spring.boot.autoconfigure.springdoc;

import cn.javaer.snippets.springdoc.PageDoc;
import cn.javaer.snippets.springdoc.PageableDoc;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 将 jOOQ 仅用于 SQL 生成时的自动配置（即：没有数据源）.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(org.springdoc.core.converters.models.Pageable.class)
@AutoConfigureAfter({SpringDocWebMvcConfiguration.class})
@ConditionalOnProperty(prefix = "snippets.springdoc", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class SpringdocAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        SpringDocUtils.getConfig().replaceWithClass(Pageable.class,
                PageableDoc.class);
        SpringDocUtils.getConfig().replaceWithClass(Page.class,
                PageDoc.class);
    }
}
