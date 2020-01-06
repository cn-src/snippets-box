package cn.javaer.snippets.box.jooq;

import cn.javaer.snippets.box.jooq.gen.tables.records.CityRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class JooqRecordIgnoreSupperIntrospectorTest {

    @Test
    void deserialization() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector());
        final CityRecord cityRecord = objectMapper.readValue("{\"name\":\"jack\"}", CityRecord.class);
        assertThat(cityRecord.getName()).isEqualTo("jack");
    }

    @Test
    void serialization() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector());
        final CityRecord cityRecord = new CityRecord();
        cityRecord.setName("Lucy");
        final String json = objectMapper.writeValueAsString(cityRecord);
        JSONAssert.assertEquals("{\"name\":\"Lucy\"}", json, false);
    }
}