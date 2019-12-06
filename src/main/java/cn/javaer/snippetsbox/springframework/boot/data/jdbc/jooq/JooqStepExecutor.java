package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

import java.util.List;
import java.util.Optional;

/**
 * @author cn-src
 */
public interface JooqStepExecutor<T> {

    Optional<T> findOne(QueryStep queryStep);

    List<T> findAll(QueryStep queryStep);
}
