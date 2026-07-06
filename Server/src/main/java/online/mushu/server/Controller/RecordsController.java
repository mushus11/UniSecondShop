package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.ChangeRecordDto;
import online.mushu.server.Dto.RecordDto;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Entity.TransactionRecords;
import online.mushu.server.Entity.User;
import online.mushu.server.Service.GoodsService;
import online.mushu.server.Service.TransactionRecordsService;
import online.mushu.server.Service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * =======
 * =======
 */
@RestController
@RequestMapping("/api/Records")
public class RecordsController {

    @Resource
    TransactionRecordsService transactionRecordsService;
    @Resource
    GoodsService goodsService;
    @Resource
    UserService userService;

    @PostMapping("/createRecord")
    public String createRecord(@RequestBody RecordDto dto) {
        String id = UUID.randomUUID().toString();
        int buyerID = dto.getBuyerID();
        int sellerID = dto.getSellerID();
        String goodID = dto.getGoodID();
        User buyer = userService.getUserById(buyerID);
        User seller = userService.getUserById(sellerID);
        Goods good = goodsService.getGoodsByID(goodID);

        TransactionRecords record = new TransactionRecords(id, buyer, seller, good, dto.getTransactionTime(), dto.getTradingLocation(), good.getPrice(), dto.getNote(), dto.isState());
        transactionRecordsService.saveRecord(record);

        return "success";
    }

    @PostMapping("/changeState")
    public String changeState(@RequestBody ChangeRecordDto dto){
        String id = dto.getId();
        TransactionRecords record = transactionRecordsService.getRecord(id);
        record.setState(!record.isState());
        transactionRecordsService.updateRecord(id, record);
        return "success";
    }

    @PostMapping("/changeRecord")
    public String changeRecord(@RequestBody RecordDto dto, @RequestParam(name = "ID") String id){
        TransactionRecords record = transactionRecordsService.getRecord(id);
        record.setNote(dto.getNote());
        record.setPrice(dto.getPrice());
        record.setTradingLocation(dto.getTradingLocation());
        record.setTransactionTime(dto.getTransactionTime());
        transactionRecordsService.updateRecord(id, record);
        return "success";
    }

    @GetMapping("/getRecordByUserID")
    public List<TransactionRecords> getRecordByUserID(@RequestParam(name = "ID") int id){
        return transactionRecordsService.getRecordsByUserID(id);
    }

    @GetMapping("/getRecordByID")
    public TransactionRecords getRecordByID(@RequestParam(name = "ID") String id){
        return transactionRecordsService.getRecord(id);
    }

    @DeleteMapping("/deleteRecord")
    public String deleteRecord(@RequestParam(name = "ID") String id){
        transactionRecordsService.delete(id);
        return "success";
    }

}
