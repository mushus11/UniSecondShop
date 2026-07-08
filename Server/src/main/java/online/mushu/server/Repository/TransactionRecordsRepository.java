package online.mushu.server.Repository;

import online.mushu.server.Entity.TransactionRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * =======
 * =======
 */
public interface TransactionRecordsRepository extends JpaRepository<TransactionRecords, String> {


    @Query("select tr from TransactionRecords tr where tr.buyer.id = :buyerID or tr.seller.id = :sellerID")
    List<TransactionRecords> findByBuyerIDAndSellerID(@Param("buyerID") int buyerID, @Param("sellerID") int sellerID);

    @Query("select tr from TransactionRecords tr where tr.seller.id = :sellerID")
    List<TransactionRecords> findBySellerID(@Param("sellerID") int sellerID);

    @Query("select tr from TransactionRecords tr where tr.buyer.id = :buyerID")
    List<TransactionRecords> findByBuyerID(@Param("buyerID") int buyerID);

    @Query("select count(tr) from TransactionRecords tr where tr.goods.type=:type and tr.state")
    int countBySellAndType(@Param("type") int type);

    List<TransactionRecords> findByStateTrue();


}
