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

    T insert(final T instance);

    <S extends T> int[] batchInsert(Iterable<S> entities);

    T update(final T instance);

    DSLContext dsl();
}
