package online.mushu.server.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Goods> goodsList = new ArrayList<>();

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PurchaseInf> purchaseInfList = new ArrayList<>();

    @OneToMany(
            mappedBy = "buyer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TransactionRecords> buyer = new ArrayList<>();

    @OneToMany(
            mappedBy = "seller",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TransactionRecords> seller = new ArrayList<>();

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserProfile userProfile;

    public User(int id,String username , String password) {
        setId(id);
        setUsername(username);
        setPassword(password);
    }

}
