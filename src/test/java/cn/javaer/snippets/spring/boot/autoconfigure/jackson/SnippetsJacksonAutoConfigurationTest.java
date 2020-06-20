package cn.javaer.snippets.spring.boot.autoconfigure.jackson;

import cn.javaer.snippets.spring.format.DateFillFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author cn-src
 */
class SnippetsJacksonAutoConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    SnippetsJacksonAutoConfiguration.class, JacksonAutoConfiguration.class));

    @Test
    void auto() throws Exception {
        this.contextRunner.run(context -> {
            final ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
            //language=JSON
            final Demo demo = objectMapper.readValue("{\"dateTime\": \"2020-05-05\"}", Demo.class);
            assertThat(demo.dateTime).isEqualTo(LocalDate.parse("2020-05-05").atTime(LocalTime.MAX));
        });
    }

    @Data
    static class Demo {
        @DateFillFormat(fillTime = DateFillFormat.FillTime.MAX)
        LocalDateTime dateTime;
    }
}