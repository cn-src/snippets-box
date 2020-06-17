package cn.javaer.snippets.box.springdoc;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

/**
 * @author cn-src
 */
public class PageableDoc {

    @Min(1)
    @Parameter(name = "_page", description = "分页-页码", schema = @Schema(type = "integer", defaultValue = "0"))
    private Integer page;

    @Min(1)
    @Parameter(name = "_size", description = "分页-大小", schema = @Schema(type = "integer", defaultValue = "20"))
    private Integer size;

    @Parameter(name = "_sort", description = "分页-排序, 指定排序字段: '_sort=field1,field2', 指定排序方式: 'field1.dir=desc'默认为升序(asc)"
            , array = @ArraySchema(schema = @Schema(type = "string")))
    private List<String> sort;

    public PageableDoc(final int page, final int size, final List<String> sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public Integer getPage() {
        return this.page;
    }

    public void setPage(final Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return this.size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public List<String> getSort() {
        return this.sort;
    }

    public void setSort(final List<String> sort) {
        if (sort == null) {
            this.sort.clear();
        }
        else {
            this.sort = sort;
        }
    }

    public void addSort(final String sort) {
        this.sort.add(sort);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final PageableDoc pageableDoc = (PageableDoc) o;
        return Objects.equals(this.page, pageableDoc.page) &&
                Objects.equals(this.size, pageableDoc.size) &&
                Objects.equals(this.sort, pageableDoc.sort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.page, this.size, this.sort);
    }

    @Override
    public String toString() {
        return "Pageable{" +
                "page=" + this.page +
                ", size=" + this.size +
                ", sort=" + this.sort +
                '}';
    }
}