package cn.javaer.snippetsbox.eclipse.collections.city;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class City {

    @Id
    private Long id;

    private String name;

    protected City() {
    }

    public City(String name) {
        this.name = name;
    }
}
