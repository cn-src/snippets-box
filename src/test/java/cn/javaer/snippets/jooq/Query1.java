package cn.javaer.snippets.jooq;

import cn.javaer.snippets.jooq.condition.annotation.ConditionContains;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.JSONB;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query1 {

    private String str1;

    @ConditionContains
    private String str2;

    private JSONB jsonb1;

    @ConditionContains
    private JSONB jsonb2;
}
