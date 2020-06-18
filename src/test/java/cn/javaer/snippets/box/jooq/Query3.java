package cn.javaer.snippets.box.jooq;

import cn.javaer.snippets.box.jooq.condition.annotation.ConditionBetweenMax;
import cn.javaer.snippets.box.jooq.condition.annotation.ConditionBetweenMin;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query3 {

    @ConditionBetweenMin("colNum")
    private Integer start;

    @ConditionBetweenMax("colNum")
    private Integer end;
}
