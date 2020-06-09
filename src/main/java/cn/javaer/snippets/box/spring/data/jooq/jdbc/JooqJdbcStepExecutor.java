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

    /**
     * 查询单个实体
     *
     * @param condition 查询条件
     *
     * @return 单个实体
     */
    Optional<T> findOne(Condition condition);

    /**
     * 查询多个实体
     *
     * @param condition 查询条件
     *
     * @return 实体 List
     */
    List<T> findAll(Condition condition);

    /**
     * 分页查询多个实体
     *
     * @param condition 查询条件
     * @param pageable 分页参数
     *
     * @return 分页结果
     */
    Page<T> findAll(Condition condition, Pageable pageable);

    /**
     * 统计实体总数
     *
     * @param condition 查询条件
     *
     * @return 总数
     */
    long count(Condition condition);

    /**
     * 判断是否存在实体
     *
     * @param condition 查询条件
     *
     * @return 存在返回 true
     */
    boolean exists(Condition condition);
}
