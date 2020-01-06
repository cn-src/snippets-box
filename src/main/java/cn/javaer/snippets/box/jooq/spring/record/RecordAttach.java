package cn.javaer.snippets.box.jooq.spring.record;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cn-src
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RecordAttach {

    /**
     * {@link org.jooq.Configuration} bean name.
     *
     * @return bean name
     */
    String value() default "";
}
