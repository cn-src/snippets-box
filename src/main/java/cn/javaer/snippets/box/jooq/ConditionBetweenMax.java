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
public @interface ConditionBetweenMax {

    /**
     * Between 的表列名，驼峰式自动转换成下划线。
     *
     * @return Between 的表列名
     */
    String value();
}
