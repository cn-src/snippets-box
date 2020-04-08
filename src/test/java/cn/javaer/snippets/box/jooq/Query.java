package cn.javaer.snippets.box.jooq;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jooq.JSONB;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query {

    private String str1;

    @ConditionContains
    private String str2;

    private JSONB jsonb1;

    @ConditionContains
    private JSONB jsonb2;
}
