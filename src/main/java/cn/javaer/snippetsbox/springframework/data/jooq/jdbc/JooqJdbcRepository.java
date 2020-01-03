package cn.javaer.snippetsbox.springframework.data.jooq.jdbc;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author cn-src
 */
@NoRepositoryBean
public interface JooqJdbcRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T>, JooqJdbcStepExecutor<T> {

    T insert(final T instance);

    T update(final T instance);
}
