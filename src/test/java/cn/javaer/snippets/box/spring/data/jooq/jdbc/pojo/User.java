package cn.javaer.snippets.box.spring.data.jooq.jdbc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author cn-src
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("T_USER")
public class User {
    @Id
    private Long id;

    private String name;

    private String gender;

    public User(final String name, final String gender) {
        this.name = name;
        this.gender = gender;
    }
}
