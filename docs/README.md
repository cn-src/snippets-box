# Snippets Box

## 介绍
收集平时开发中可以重用的代码片段，以 jar 发布可以方便快速集成使用。
详细的样例可参考单元测试代码。

## Maven
1. 添加 JitPack 仓库
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2. 添加依赖

[![jitpack](https://jitpack.io/v/cn-src/snippets-box.svg)](https://jitpack.io/#cn-src/snippets-box)

```xml
<dependency>
    <groupId>com.github.cn-src</groupId>
    <artifactId>snippets-box</artifactId>
    <version>Tag</version>
</dependency>
```