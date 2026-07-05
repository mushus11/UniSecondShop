package online.mushu.server.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * =======
 * =======
 */
@Entity
@Table(name = "PurchaseInf")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInf {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_purchase_inf_user",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (userID) REFERENCES User(ID) "
            )
    )
    private User user;

    @Column(name = "goodsName")
    private String goodsName;
    @Column(name = "type")
    private int type;
    @Lob
    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "purchaseTime")
    private Timestamp purchaseTime;
    @Column(name = "price")
    private double price;

}
