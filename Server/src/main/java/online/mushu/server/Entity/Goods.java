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
@Table(name = "Goods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Goods {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_goods_user",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (userID) REFERENCES User(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private User user;

    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private int type;
    @Column(name = "price")
    private double price;
    @Lob
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;
    @Column(name = "state", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean state;




}
