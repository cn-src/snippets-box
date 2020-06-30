# Spring Data JDBC

## eclipse-collections

支持 `Repository` 接口返回值里直接使用 **eclipse-collections** 框架的集合类型

```java
public interface CityRepository extends CrudRepository<City, Long> {
    ImmutableList<City> findAll();
}
```

## 批量插入
```java
<S extends T> int[] batchInsert(Iterable<S> entities);
```

## jOOQ 支持

```java
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
```

## 审计条件支持
```java
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
     * 创建者为当前用户查找。
     *
     * @param pageable 分页和排序参数
     *
     * @return Page<T>
     */
    Page<T> findAllByCreator(Pageable pageable);

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
```