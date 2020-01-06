package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import java.util.List;
import java.util.Optional;

/**
 * @author cn-src
 */
public interface JooqJdbcStepExecutor<T> {

    Optional<T> findOne(QueryStep queryStep);

    List<T> findAll(QueryStep queryStep);
}
