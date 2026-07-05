package online.mushu.server.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * =======
 * =======
 */
@Entity
@Table(name = "Goods")
@Data
@NoArgsConstructor
public class Goods {

    @Id
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_goods_user",
                    value = ConstraintMode.PROVIDER_DEFAULT,
                    foreignKeyDefinition = "FOREIGN KEY (userID) REFERENCES User(ID) "
            )
    )
    private User user;

    @OneToMany(
            mappedBy = "goods",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<GoodImages> images = new ArrayList<>();

    @OneToMany(
            mappedBy = "goods",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReleaseInf> releasesInfList = new ArrayList<>();

    @OneToMany(
            mappedBy = "goods",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TransactionRecords> transactionRecordsList  = new ArrayList<>();

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

    public Goods(String id, User user, String name, int type, double price, String text, boolean state) {
        setId(id);
        setUser(user);
        setName(name);
        setType(type);
        setPrice(price);
        setText(text);
        setState(state);
    }

}
