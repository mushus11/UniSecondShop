package online.mushu.server.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======DAO类
 * =======表user的映射类
 */

@Table(name="User")
@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    public User(int id,String username , String password) {
        setID(id);
        setUsername(username);
        setPassword(password);
    }

}
