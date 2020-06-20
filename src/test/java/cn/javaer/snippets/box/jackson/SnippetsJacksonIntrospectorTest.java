package cn.javaer.snippets.box.jackson;

import cn.javaer.snippets.box.spring.format.DateFillFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SnippetsJacksonIntrospectorTest {
    @Test
    void name() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final SnippetsJacksonIntrospector jacksonIntrospector = new SnippetsJacksonIntrospector();
        objectMapper.setAnnotationIntrospector(jacksonIntrospector);
        //language=JSON
        final Demo demo = objectMapper.readValue("{\"dateTime\": \"2020-05-05\"}", Demo.class);
        assertThat(demo.dateTime).isEqualTo(LocalDate.parse("2020-05-05").atTime(LocalTime.MIN));
    }

    @Data
    static class Demo {
        @DateFillFormat(fillTime = DateFillFormat.FillTime.MIN)
        LocalDateTime dateTime;
    }
}