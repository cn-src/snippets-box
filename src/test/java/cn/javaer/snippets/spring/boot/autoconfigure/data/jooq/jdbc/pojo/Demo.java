package cn.javaer.snippets.spring.boot.autoconfigure.data.jooq.jdbc.pojo;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.JSONB;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class Demo {
    public Demo(final JSONB jsonb1, final JsonNode jsonb2) {
        this.jsonb1 = jsonb1;
        this.jsonb2 = jsonb2;
    }

    @Id
    Long id;
    JSONB jsonb1;
    JsonNode jsonb2;
}