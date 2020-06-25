package cn.javaer.snippets;

import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

/**
 * @author cn-src
 */
public class TestPostgreSQL {
    private static final PostgreSQLContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer("postgres:10-alpine");

    static {
        POSTGRESQL_CONTAINER.start();
    }

    public static DataSource createDataSource() {

        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(POSTGRESQL_CONTAINER.getJdbcUrl());
        dataSource.setUser(POSTGRESQL_CONTAINER.getUsername());
        dataSource.setPassword(POSTGRESQL_CONTAINER.getPassword());

        return dataSource;
    }

    public static DataSource createDataSource(final DataSourceInfo info) {

        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(info.getJdbcUrl());
        dataSource.setUser(info.getUsername());
        dataSource.setPassword(info.getPassword());

        return dataSource;
    }

    public static DataSourceInfo createDataSourceInfo() {
        return DataSourceInfo.builder()
                .jdbcUrl(POSTGRESQL_CONTAINER.getJdbcUrl())
                .username(POSTGRESQL_CONTAINER.getUsername())
                .password(POSTGRESQL_CONTAINER.getPassword())
                .build();
    }
}
