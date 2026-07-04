package online.mushu.server.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {


    @Id
    @Column(name = "ID")
    private int ID;
    @Column(name = "college")
    private String college;
    @Column(name = "grade")
    private String grade;
    @Column(name = "telephone", columnDefinition = "CHAR(11) NOT NULL")
    private String telephone;
    @Column(name = "profile", columnDefinition = "TEXT DEFAULT ' '")
    private String profile;
    @Column(name = "certified", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean certified;
    @Lob
    @Column(name = "image")
    private String image;

    public UserProfile(int id) {
        setID(id);
    }

}
