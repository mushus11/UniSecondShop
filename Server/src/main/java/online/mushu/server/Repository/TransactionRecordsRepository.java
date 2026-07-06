package online.mushu.server.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import online.mushu.server.Entity.TransactionRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * =======
 * =======
 */
public interface TransactionRecordsRepository extends JpaRepository<TransactionRecords, String> {


    @Modifying
    @Query("select tr from TransactionRecords tr where tr.buyer.id = :buyerID or tr.seller.id = :sellerID")
    List<TransactionRecords> findByBuyerIDAndSellerID(@Param("buyerID") int buyerID, @Param("sellerID") int sellerID);

    @Modifying
    @Query("select tr from TransactionRecords tr where tr.seller.id = :sellerID")
    List<TransactionRecords> findBySellerID(@Param("sellerID") int sellerID);

    @Modifying
    @Query("select tr from TransactionRecords tr where tr.buyer.id = :buyerID")
    List<TransactionRecords> findByBuyerID(@Param("buyerID") int buyerID);


}
