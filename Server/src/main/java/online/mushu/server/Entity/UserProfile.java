package online.mushu.server.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * =======
 * =======
 */

@Entity
@Table(name = "UserProfile")
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
    @Column(name = "telephone", columnDefinition = "CHAR(11)")
    private String telephone;
    @Column(name = "profile", columnDefinition = "TEXT")
    private String profile;
    @Column(name = "certified", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean certified;
    @Column(name = "access")
    private String access;
    @Lob
    @Column(name = "image")
    private String image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_profile_user",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (userID) REFERENCES User(ID) "
            )
    )
    private User user;

    public UserProfile(int id, User user) {
        setID(id);
        setUser(user);
    }

}
