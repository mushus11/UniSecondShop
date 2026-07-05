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
@Table(name = "GoodImages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodImages {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goodID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_good_images_goods",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (goodID) REFERENCES Goods(ID) " +
                            "ON DELETE CASCADE"
            )
    )
    private Goods goods;

    @Column(name = "voucher", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean voucher;
}
