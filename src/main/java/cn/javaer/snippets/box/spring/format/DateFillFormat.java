package cn.javaer.snippets.box.spring.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cn-src
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateFillFormat {

    String style() default "SS";

    String pattern() default "yyyy-MM-dd";

    String printPattern() default "yyyy-MM-dd HH:mm:ss";

    FillTime fillTime();

    enum FillTime {
        /**
         *
         */
        MIN,
        /**
         *
         */
        MAX
    }
}
