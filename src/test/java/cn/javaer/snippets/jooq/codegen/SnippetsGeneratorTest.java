package cn.javaer.snippets.jooq.codegen;

import cn.javaer.snippets.DataSourceInfo;
import cn.javaer.snippets.TestPostgreSQL;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.jooq.meta.postgres.PostgresDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author cn-src
 */
class SnippetsGeneratorTest {

    @Test
    void generateTable() throws Exception {
        final DataSourceInfo dataSourceInfo = TestPostgreSQL.createDataSourceInfo();
        final DataSource dataSource = TestPostgreSQL.createDataSource(dataSourceInfo);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // language=PostgreSQL
        jdbcTemplate.execute("CREATE TABLE demo" +
                "(" +
                "id bigserial NOT NULL" +
                " CONSTRAINT demo_pkey" +
                " PRIMARY KEY," +
                "jsonb1 jsonb" +
                ");");
        final Configuration configuration = new Configuration()
                .withJdbc(new Jdbc()
                        .withDriver("org.postgresql.Driver")
                        .withUrl(dataSourceInfo.getJdbcUrl())
                        .withUser(dataSourceInfo.getUsername())
                        .withPassword(dataSourceInfo.getPassword())
                )
                .withGenerator(new Generator()
                        .withName(SnippetsGenerator.class.getName())
                        .withDatabase(new Database()
                                .withName(PostgresDatabase.class.getName())
                                .withInputSchema("public")
                                .withIncludes(".*")
                                .withIncludeRoutines(false)
                                .withIncludePackages(false)
                                .withIncludePackageRoutines(false)
                                .withIncludePackageUDTs(false)
                                .withIncludePackageConstants(false)
                                .withIncludeUDTs(false)
                                .withIncludeSequences(false)
                                .withTableValuedFunctions(false)
                                .withExcludes("spatial_ref_sys|geography_columns|geometry_columns|raster_columns|raster_overviews")
                        )
                        .withTarget(new Target()
                                .withClean(true)
                                .withPackageName("test.gen")
                                .withDirectory(System.getProperty("user.dir") + "/src/test/java")
                        ).withGenerate(new Generate()
                                .withRecords(false))
                );
        GenerationTool.generate(configuration);
    }
}