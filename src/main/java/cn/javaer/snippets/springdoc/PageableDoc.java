package cn.javaer.snippets.springdoc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author cn-src
 */
@Data
@Schema(name = "Pageable")
public class PageableDoc {

    @Schema(name = "_page",
            description = "分页-页码",
            minimum = "1",
            defaultValue = "1")
    Integer page;

    @Schema(name = "_size",
            description = "分页-大小",
            minimum = "1",
            defaultValue = "20")
    Integer size;

    @Schema(name = "_sort",
            description = "分页-排序, 指定排序字段: '_sort=field1,field2', 指定排序方式: 'field1.dir=desc'默认为升序(asc)"
    )
    List<String> sort;
}