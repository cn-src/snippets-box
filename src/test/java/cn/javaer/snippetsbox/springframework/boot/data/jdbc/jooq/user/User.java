package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq.user;

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
@Table("t_user")
public class User {
    @Id
    private Long id;

    private String name;

    private String gender;

    public User(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }
}
