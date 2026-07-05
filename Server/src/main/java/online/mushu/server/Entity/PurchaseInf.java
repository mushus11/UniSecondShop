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
    private String ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_purchase_inf_user",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (userID) REFERENCES User(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private User user;

    @Column(name = "type")
    private int type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goodID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_purchase_inf_goods",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (goodID) REFERENCES Goods(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private Goods goods;
    @Column(name = "purchaseTime")
    private Timestamp purchaseTime;
    @Column(name = "price")
    private double price;

}
