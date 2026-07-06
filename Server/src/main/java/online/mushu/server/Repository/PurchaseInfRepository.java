package online.mushu.server.Repository;

import online.mushu.server.Entity.PurchaseInf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * =======
 * =======
 */
public interface PurchaseInfRepository extends JpaRepository<PurchaseInf, String> {

    List<PurchaseInf> findAllByType(int type);

    @Modifying
    @Query("select pi from PurchaseInf pi where pi.user.id = :id")
    List<PurchaseInf> findByUserID(int id);

}
