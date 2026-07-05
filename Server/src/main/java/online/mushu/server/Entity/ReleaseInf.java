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
@Table(name = "ReleaseInf")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseInf {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "goodID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_release_inf_good",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (goodID) REFERENCES Goods(ID) "
            )
    )
    private Goods goods;

    @Column(name = "upLoadTime")
    private Timestamp upLoadTime;
    @Column(name = "downLoadTime")
    private Timestamp downLoadTime;
    @Column(name = "state")
    private int state;

    @Column(name = "hurryMark", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean hurryMark;
    @Column(name = "topMark", columnDefinition = "TINYINT(1) NOT NULL DEFAULT 0")
    private boolean topMark;
}
