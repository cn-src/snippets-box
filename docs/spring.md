# Spring

## 事务注解
提供一组与事物有关的注解，默认 `rollbackFor = Throwable.class`
> Spring 默认不回滚受检查异常，会造成无法预计的情况。比如 lombok 的 `@SneakyThrows` 实现方式就是一种可避开编译器检查的方式。

* `@Tx`
* `@TxReadOnly`
* `@TxReadOnlyService`
* `@TxService`

## 请求/响应日志
提供 `RequestResponseLoggingFilter` 可以在开发和测试阶段打印可读性良好的详细请求和响应日志。
