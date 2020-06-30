# 日期时间
提供 `@DateFillFormat` 注解，可以填充只有日期而没有时间的时间部分，
比如 `2020-01-01` 填充一天的开始时间 `00:00:00`，支持 spring 和 jackson

```java
@DateFillFormat(fillTime = DateFillFormat.FillTime.MIN)
LocalDateTime dateTime;
```