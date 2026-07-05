package online.mushu.server.Service;

import online.mushu.server.Entity.TransactionRecords;
import online.mushu.server.Repository.TransactionRecordsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =======
 * =======
 */
@Service
public class TransactionRecordsService {

    TransactionRecordsRepository transactionRecordsRepository;
    public TransactionRecordsService(TransactionRecordsRepository transactionRecordsRepository) {
        this.transactionRecordsRepository = transactionRecordsRepository;
    }

    public void saveRecord(TransactionRecords transactionRecords) {
        transactionRecordsRepository.save(transactionRecords);
    }

    public TransactionRecords getRecord(String id) {
        return transactionRecordsRepository.findById(id).orElse(null);
    }

    public void updateRecord(String id, TransactionRecords transactionRecords) {
        transactionRecordsRepository.save(transactionRecords);
    }

    public List<TransactionRecords> getRecordsByUserID(int id) {
        return transactionRecordsRepository.findByBuyerIDAndSellerID(id, id);
    }

    public void delete(String id) {
        transactionRecordsRepository.deleteById(id);
    }
}
