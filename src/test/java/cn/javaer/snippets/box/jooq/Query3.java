package cn.javaer.snippets.box.jooq;

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
