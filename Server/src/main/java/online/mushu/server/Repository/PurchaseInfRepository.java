package online.mushu.server.Repository;

import online.mushu.server.Entity.PurchaseInf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * =======
 * =======
 */
public interface PurchaseInfRepository extends JpaRepository<PurchaseInf, String> {

    PurchaseInf findByID(String id);

    PurchaseInf updateByID(String id , PurchaseInf purchaseInf);

    List<PurchaseInf> findAllByType(int type);

    List<PurchaseInf> findByUserID(int id);

    PurchaseInf deleteByID(String id);
}
