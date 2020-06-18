package cn.javaer.snippets.box.jooq;

import cn.javaer.snippets.box.jooq.condition.annotation.ConditionContains;
import cn.javaer.snippets.box.jooq.condition.annotation.ConditionEqual;
import lombok.Data;

/**
 * @author cn-src
 */
@Data
public class ExceptionQuery {

    @ConditionEqual
    @ConditionContains
    private final String str1;
}
