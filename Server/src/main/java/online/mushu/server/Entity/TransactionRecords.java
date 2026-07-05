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
@Table(name = "TransactionRecords")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecords {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "buyerID",

            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_transaction_records_user_buyers",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (buyerID) REFERENCES User(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sellerID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_transaction_records_user_seller",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (sellerID) REFERENCES User(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goodID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_transaction_records_goods",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (goodID) REFERENCES Goods(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private Goods good;

    @Column(name = "transactionTime")
    private Timestamp transactionTime;
    @Column(name = "tradingLocation")
    private String tradingLocation;
    @Column(name = "price")
    private double price;
    @Lob
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    @Column(name = "state", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 1")
    private boolean state;
}
