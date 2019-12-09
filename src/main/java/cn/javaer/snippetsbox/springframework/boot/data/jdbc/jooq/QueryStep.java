package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

import org.jooq.DSLContext;
import org.jooq.ResultQuery;
import org.jooq.TableLike;

/**
 * @author cn-src
 */
public interface QueryStep {

    @SuppressWarnings("rawtypes")
    <Q extends TableLike & ResultQuery> Q step(DSLContext dsl);
}
