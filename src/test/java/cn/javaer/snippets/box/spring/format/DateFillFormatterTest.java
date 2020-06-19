package cn.javaer.snippets.box.spring.format;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static cn.javaer.snippets.box.test.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author cn-src
 */
class DateFillFormatterTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(MockMvcAutoConfiguration.class, WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
                            HttpMessageConvertersAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class));

    @Test
    void fillTime() {
        this.contextRunner.withUserConfiguration(TestController.class, WebConfig.class)
                .run(context -> {
                    final MockMvc mockMvc = context.getBean(MockMvc.class);
                    mockMvc.perform(get("/test?dateTime=2020-01-01")).andExpect(status().isOk());
                });
    }

    @Configuration
    static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addFormatters(final FormatterRegistry registry) {
            registry.addFormatterForFieldAnnotation(new DateFillFormatter());
        }
    }

    @RestController
    @RequestMapping
    static class TestController {

        @GetMapping("test")
        public String get(@DateFillFormat(fillTime = DateFillFormat.FillTime.MAX)
                          @RequestParam final LocalDateTime dateTime) {
            assertThat(dateTime).isEqualTo(LocalDate.parse("2020-01-01").atTime(LocalTime.MAX));
            return dateTime.toString();
        }
    }
}