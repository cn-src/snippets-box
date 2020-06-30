# jOOQ

## ConditionBuilder

构建 jOOQ 的查询条件 `Condition`，`ConditionBuilder` 构建条件时会自动排除值为空的条件
（参数为`Supplier<Condition>` 除外）。

```java
Condition condition = new ConditionBuilder()
        .append(FIELD::contains, "value")
        .append(FIELD::eq, "value")
        .build();
```

## ConditionCreator

构建 jOOQ 的查询条件 `Condition`，支持的注解：
* `@ConditionEqual`
* `@ConditionContains`
* `@ConditionContained` 被包含
* `@ConditionLessThan`
* `@ConditionLessOrEqual`
* `@ConditionGreaterThan`
* `@ConditionGreaterOrEqual`
* `@ConditionBetweenMin`
* `@ConditionBetweenMax`
* `@ConditionIgnore`

基于POJO的动态条件构建
```java
public class Query {

    private String str1;

    @ConditionContains
    private String str2;
}

Condition condition = ConditionCreator.create(new Query("str1", "str2"));
```

Tree 树形组件的动态条件

```java
List<TreeNode> treeNodes = new ArrayList<>();

Condition condition = ConditionCreator.create(treeNodes,
        DSL.field("f1", String.class),
        DSL.field("f2", String.class),
        DSL.field("f3", String.class));
```
