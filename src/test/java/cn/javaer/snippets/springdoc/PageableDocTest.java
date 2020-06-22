package cn.javaer.snippets.springdoc;

import org.junit.jupiter.api.Test;
import org.springdoc.core.Constants;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.webmvc.core.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.SpringDocWebMvcConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author cn-src
 */
class PageableDocTest {
    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(
                    AutoConfigurations.of(SpringDocConfigProperties.class, SpringDocConfiguration.class, MultipleOpenApiSupportConfiguration.class, SpringDocWebMvcConfiguration.class, MockMvcAutoConfiguration.class, WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class,
                            HttpMessageConvertersAutoConfiguration.class));

    @Test
    void generateDoc() {
        this.contextRunner.withUserConfiguration(DemoController.class)
                .run(context -> {
                    final MockMvc mockMvc = context.getBean(MockMvc.class);
                    SpringDocUtils.getConfig().replaceWithClass(Pageable.class,
                            PageableDoc.class);
                    SpringDocUtils.getConfig().replaceWithClass(Page.class,
                            PageDoc.class);
                    final MvcResult mvcResult = mockMvc.perform(get(Constants.DEFAULT_API_DOCS_URL)).andExpect(status().isOk())
                            .andExpect(jsonPath("$.components.schemas.Pageable.properties._page.description", is("分页-页码"))).andReturn();
//                    System.out.println(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8));
                });
    }

    @RestController
    @RequestMapping
    static class DemoController {

        @GetMapping("test")
        public Page<String> get(final Pageable pageable) {
            return new PageImpl<>(Collections.singletonList("page data"), pageable, 1);
        }
    }
}