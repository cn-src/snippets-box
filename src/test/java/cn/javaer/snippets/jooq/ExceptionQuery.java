package cn.javaer.snippets.jooq;

import cn.javaer.snippets.jooq.condition.annotation.ConditionContains;
import cn.javaer.snippets.jooq.condition.annotation.ConditionEqual;
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
