package cn.javaer.snippetsbox.eclipse.collections.city;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class City {

    @Id
    private Long id;

    private String name;

    protected City() {
    }

    public City(final String name) {
        this.name = name;
    }
}
