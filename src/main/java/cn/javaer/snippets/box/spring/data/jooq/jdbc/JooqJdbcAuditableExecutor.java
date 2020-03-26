package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import java.util.Optional;

/**
 * @author cn-src
 */
public interface JooqJdbcAuditableExecutor<T, ID> {

    Optional<T> findByIdAndCreator(final ID id);

    Iterable<T> findAllByCreator();

    /**
     * 更新实体，根据实体 ID 和 创建者。
     *
     * @param instance 实体
     *
     * @return 实体
     */
    T updateByIdAndCreator(final T instance);

    /**
     * 删除实体，根据实体 ID 和 创建者。
     *
     * @param id 实体 ID
     */
    void deleteByIdAndCreator(final ID id);
}
