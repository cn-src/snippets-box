package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.Condition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * @author cn-src
 */
public interface JooqJdbcStepExecutor<T> {

    Optional<T> findOne(QueryStep queryStep);

    List<T> findAll(QueryStep queryStep);

    Optional<T> findOne(Condition condition);

    List<T> findAll(Condition condition);

    Page<T> findAll(Condition condition, Pageable pageable);
}
