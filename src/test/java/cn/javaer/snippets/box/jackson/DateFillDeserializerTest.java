package cn.javaer.snippets.box.jackson;

import cn.javaer.snippets.box.spring.format.DateFillFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * @author cn-src
 */
class DateFillDeserializerTest {

    @Test
    void deserialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        //language=JSON
        final Demo demo = objectMapper.readValue("{\"dateTime\": \"2020-05-05\"}", Demo.class);
        System.out.println(demo);
    }

    @Data
    static class Demo {
        @DateFillFormat(fillTime = DateFillFormat.FillTime.MIN)
        @JsonDeserialize(using = DateFillDeserializer.class)
        LocalDateTime dateTime;
    }
}