package cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc;

import cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc.pojo.Demo;
import cn.javaer.snippets.spring.data.jooq.jdbc.JooqJdbcRepository;

public interface DemoRepository extends JooqJdbcRepository<Demo, Long> {

}