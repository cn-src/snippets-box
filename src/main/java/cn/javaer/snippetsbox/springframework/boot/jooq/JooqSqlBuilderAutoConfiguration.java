package cn.javaer.snippetsbox.springframework.boot.jooq;

import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

/**
 * 将 jOOQ 仅用于 SQL 生成时的自动配置（即：没有数据源）.
 *
 * @author cn-src
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DSLContext.class)
@ConditionalOnMissingBean(DSLContext.class)
public class JooqSqlBuilderAutoConfiguration {
}
