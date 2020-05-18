package cn.javaer.snippets.box.spring.data.jooq.jdbc;

import org.jooq.DSLContext;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author cn-src
 */
@NoRepositoryBean
public interface JooqJdbcRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T>, JooqJdbcStepExecutor<T>, JooqJdbcAuditableExecutor<T, ID> {

    /**
     * 插入实体
     *
     * @param instance 实体
     *
     * @return 实体
     */
    T insert(final T instance);

    /**
     * 批量插入实体
     *
     * @param entities 实体
     * @param <S> 实体类型
     *
     * @return 受影响行数
     */

    <S extends T> int[] batchInsert(Iterable<S> entities);

    /**
     * 更新实体
     *
     * @param instance 实体
     *
     * @return 实体
     */
    T update(final T instance);

    /**
     * 返回 DSLContext
     *
     * @return DSLContext
     */
    DSLContext dsl();
}
