package cn.javaer.snippets.box.jooq;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author cn-src
 */
@Data
@AllArgsConstructor
public class Query4 {

    @ConditionBetweenMin(column = "colName", dateToDateTime = true)
    private LocalDate start;

    @ConditionBetweenMax(column = "colName", dateToDateTime = true)
    private LocalDate end;
}
