package cn.javaer.snippets.box.jooq;

import cn.javaer.snippets.box.jooq.condition.annotation.ConditionContains;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query2 {

    private String str1;

    @ConditionContains
    private String str2;
}
