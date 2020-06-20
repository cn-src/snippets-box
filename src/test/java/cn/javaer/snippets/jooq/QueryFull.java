package cn.javaer.snippets.jooq;

import cn.javaer.snippets.jooq.condition.annotation.ConditionBetweenMax;
import cn.javaer.snippets.jooq.condition.annotation.ConditionBetweenMin;
import cn.javaer.snippets.jooq.condition.annotation.ConditionContained;
import cn.javaer.snippets.jooq.condition.annotation.ConditionContains;
import cn.javaer.snippets.jooq.condition.annotation.ConditionEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionGreaterOrEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionGreaterThan;
import cn.javaer.snippets.jooq.condition.annotation.ConditionLessOrEqual;
import cn.javaer.snippets.jooq.condition.annotation.ConditionLessThan;
import lombok.Builder;
import lombok.Data;
import org.jooq.JSONB;

import java.time.LocalDate;

/**
 * @author cn-src
 */
@Data
@Builder
public class QueryFull {

    @ConditionEqual
    private String str1;

    @ConditionContains
    private String str2;

    @ConditionContains
    private String[] str3;

    @ConditionContains
    private JSONB jsonb1;

    @ConditionContained
    private String[] str4;

    @ConditionLessThan
    private Integer num1;

    @ConditionLessOrEqual
    private Integer num2;

    @ConditionGreaterThan
    private Integer num3;

    @ConditionGreaterOrEqual
    private Integer num4;

    @ConditionBetweenMin(column = "colDate", dateToDateTime = true)
    private LocalDate startDate;

    @ConditionBetweenMax(column = "colDate", dateToDateTime = true)
    private LocalDate endDate;

    @ConditionBetweenMin("colNum")
    private Integer startNum;

    @ConditionBetweenMax("colNum")
    private Integer endNum;
}
