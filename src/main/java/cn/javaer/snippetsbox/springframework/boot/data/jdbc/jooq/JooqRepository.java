package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

/**
 * @author cn-src
 */
@NoRepositoryBean
public interface JooqRepository<T, ID> extends PagingAndSortingRepository<T, ID>, QueryByExampleExecutor<T>, JooqStepExecutor<T> {

}
