package cn.javaer.snippetsbox.jooq;

import cn.javaer.snippetsbox.jooq.gen.tables.records.CityRecord;
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector());
        CityRecord cityRecord = objectMapper.readValue("{\"name\":\"jack\"}", CityRecord.class);
        assertThat(cityRecord.getName()).isEqualTo("jack");
    }

    @Test
    void serialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setAnnotationIntrospector(new JooqRecordIgnoreSupperIntrospector());
        CityRecord cityRecord = new CityRecord();
        cityRecord.setName("Lucy");
        String json = objectMapper.writeValueAsString(cityRecord);
        JSONAssert.assertEquals("{\"name\":\"Lucy\"}", json, false);
    }
}