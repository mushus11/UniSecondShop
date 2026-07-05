package online.mushu.server.Service;

import jakarta.annotation.Resource;
import online.mushu.server.Entity.PurchaseInf;
import online.mushu.server.Repository.PurchaseInfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =======
 * =======
 */
@Service
public class PurchaseInfService {

    @Resource
    PurchaseInfRepository repository;
    public PurchaseInfService(PurchaseInfRepository repository) {
        this.repository = repository;
    }

    public void savePurchase(PurchaseInf purchaseInf) {
        repository.save(purchaseInf);
    }

    public PurchaseInf getPurchaseById(String id) {
        return repository.findByID(id);
    }

    public void updatePurchase(PurchaseInf purchaseInf) {
        repository.updateByID(purchaseInf.getID(), purchaseInf);
    }

    public List<PurchaseInf> getByType(int type) {
        return repository.findAllByType(type);
    }

    public List<PurchaseInf> getByUserID(int userID) {
        return repository.findByUserID(userID);
    }

    public void deletePurchase(String id) {
        repository.deleteByID(id);
    }
}
