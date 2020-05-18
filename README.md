[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/cn-src/snippets-box.svg?branch=master)](https://travis-ci.org/cn-src/snippets-box)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/643d3ca00a044ebc98de3ab6da52c93f)](https://www.codacy.com/manual/cn-src/snippets-box?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cn-src/snippets-box&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/gh/cn-src/snippets-box/branch/master/graph/badge.svg)](https://codecov.io/gh/cn-src/snippets-box)
[![jitpack](https://jitpack.io/v/cn-src/snippets-box.svg)](https://jitpack.io/#cn-src/snippets-box)

# Snippets Box
> 一些零散的代码片段，集成 jar 后可直接使用

## Eclipse Collections
* 支持 **spring data Repository** 接口返回值里直接使用 **eclipse-collections** 框架的集合类型
```java
public interface CityRepository extends CrudRepository<City, Long> {
    ImmutableList<City> findAll();
}
```

* 支持代码生成的 Record 类型，jackson 序列化支持

## SimpleFlatMapper jOOQ 
* 自动整合 **SimpleFlatMapper jOOQ** 的 **JooqMapperFactory** 配置

## Spring Transaction
* 提供一组与事物有关的注解，默认 `rollbackFor = Throwable.class`
> Spring 默认不回滚受检查异常，会造成无法预计的情况。比如 lombok 的 `@SneakyThrows` 实现方式就是一种可避开编译器检查的方式。
                              
## Spring Data JDBC 支持(基于 jOOQ 实现)
* ~~分页排序 `PagingAndSortingRepository` 接口的支持~~(2.3已官方默认实现)
* 样例查询 `QueryByExampleExecutor` 接口的支持

## Kryo
* Kryo [池化封装](https://github.com/EsotericSoftware/kryo#pooling)
```java
class Demo {
    KryoHelper kryoHelper = new KryoHelper(kryo -> {
        kryo.register(User.class);
    });

    void demo() {
        byte[] bytes = kryoHelper.writeClassAndObject(user);
        User user = kryoHelper.readClassAndObject(bytes);
    }
}
```  

## 数据结构
* Tree 结构，主要用于前端的 Tree 类型组件所需的数据结构，用于"**二维表结构 <==> 树结构**"之间转换
