package cn.javaer.snippets.box.spring.data.jooq.jdbc;

/**
 * @author cn-src
 */
public interface JooqJdbcAuditableExecutor<T, ID> {

    /**
     * 更新实体，根据实体 ID 和 创建者。
     *
     * @param instance 实体
     *
     * @return 实体
     */
    T updateByIdAndCreator(final T instance);

    void deleteByIdAndCreator(final ID id);
}
