package cn.javaer.snippets.box.jooq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author cn-src
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionBetweenMin {

    /**
     * Between 的表列名，驼峰式自动转换成下划线。
     *
     * @return Between 的表列名
     */
    String value() default "";

    /**
     * @see #value()
     */
    String column() default "";

    /**
     * 如果是日期类型，则将 LocalDate 转成 LocalDateTime
     *
     * @return true 则进行转换
     */
    boolean dateToDateTime() default false;
}
