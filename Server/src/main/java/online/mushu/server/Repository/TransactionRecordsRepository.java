package online.mushu.server.Repository;

import online.mushu.server.Entity.TransactionRecords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * =======
 * =======
 */
public interface TransactionRecordsRepository extends JpaRepository<TransactionRecords, String> {

    TransactionRecords findByID(String transactionId);

    TransactionRecords updateByID(String transactionId, TransactionRecords transactionRecords);

    List<TransactionRecords> findByBuyerIDAndSellerID(int buyerID, int sellerID);

    TransactionRecords deleteByID(String transactionId);

}
