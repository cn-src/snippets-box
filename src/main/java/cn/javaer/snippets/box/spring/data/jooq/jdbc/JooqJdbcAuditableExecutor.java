package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import java.util.Optional;

/**
 * 具有审计字段的实体相关操作.
 *
 * @author cn-src
 */
public interface JooqJdbcAuditableExecutor<T, ID> {

    /**
     * 根据 id 以及创建者为当前用户查找。
     *
     * @param id id
     *
     * @return T
     */
    Optional<T> findByIdAndCreator(final ID id);

    /**
     * 创建者为当前用户查找。
     *
     * @return Iterable<T>
     */
    Iterable<T> findAllByCreator();

    /**
     * 更新实体，根据实体 ID 和 创建者为当前用户。
     *
     * @param instance 实体
     *
     * @return 实体
     */
    T updateByIdAndCreator(final T instance);

    /**
     * 删除实体，根据实体 ID 和 创建者为当前用户。
     *
     * @param id 实体 ID
     */
    void deleteByIdAndCreator(final ID id);
}
