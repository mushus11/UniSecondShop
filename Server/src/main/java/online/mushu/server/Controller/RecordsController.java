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
import online.mushu.server.Vo.RecordsVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public int changeState(@RequestBody ChangeRecordDto dto){
        String id = dto.getId();
        TransactionRecords record = transactionRecordsService.getRecord(id);
        record.setState(!record.isState());
        transactionRecordsService.updateRecord(record);
        return 200;
    }

    @PostMapping("/changeRecord")
    public int changeRecord(@RequestBody RecordDto dto, @RequestParam(name = "ID") String id){
        TransactionRecords record = transactionRecordsService.getRecord(id);
        record.setNote(dto.getNote());
        record.setPrice(dto.getPrice());
        record.setTradingLocation(dto.getTradingLocation());
        record.setTransactionTime(dto.getTransactionTime());
        transactionRecordsService.updateRecord(record);
        return 200;
    }

    @GetMapping("/getRecordByUserID")
    public List<RecordsVo> getRecordByUserID(@RequestParam(name = "ID") int id){
        List<TransactionRecords> list = transactionRecordsService.getRecordsByUserID(id);
        List<RecordsVo> recordsVos = new ArrayList<>();
        for (TransactionRecords records : list) {
            RecordsVo vo = RecordsVo.builder()
                    .id(records.getId())
                    .buyerId(records.getBuyer().getId())
                    .sellerId(records.getSeller().getId())
                    .goodID(records.getGoods().getId())
                    .transactionTime(records.getTransactionTime())
                    .tradingLocation(records.getTradingLocation())
                    .state(records.isState())
                    .price(records.getPrice())
                    .build();
            recordsVos.add(vo);
        }
        return recordsVos;
    }

    @GetMapping("/getRecordByID")
    public RecordsVo getRecordByID(@RequestParam(name = "ID") String id){
        TransactionRecords records = transactionRecordsService.getRecord(id);
        return RecordsVo.builder()
                .id(records.getId())
                .buyerId(records.getBuyer().getId())
                .sellerId(records.getSeller().getId())
                .goodID(records.getGoods().getId())
                .transactionTime(records.getTransactionTime())
                .tradingLocation(records.getTradingLocation())
                .state(records.isState())
                .price(records.getPrice())
                .node(records.getNote())
                .build();
    }

    @GetMapping("/getRecordBySellerID")
    public List<RecordsVo> getRecordBySellerID(@RequestParam(name = "ID") int id){
        List<TransactionRecords> list = transactionRecordsService.getRecordsBySellerID(id);
        List<RecordsVo> recordsVos = new ArrayList<>();
        for (TransactionRecords records : list) {
            RecordsVo vo = RecordsVo.builder()
                    .id(records.getId())
                    .buyerId(records.getBuyer().getId())
                    .sellerId(records.getSeller().getId())
                    .goodID(records.getGoods().getId())
                    .transactionTime(records.getTransactionTime())
                    .tradingLocation(records.getTradingLocation())
                    .state(records.isState())
                    .price(records.getPrice())
                    .build();
            recordsVos.add(vo);
        }
        return recordsVos;
    }

    @GetMapping("/getRecordByBuyerID")
    public List<RecordsVo> getRecordByBuyerID(@RequestParam(name = "ID") int id){
        List<TransactionRecords> list = transactionRecordsService.getRecordsByBuyerID(id);
        List<RecordsVo> recordsVos = new ArrayList<>();
        for (TransactionRecords records : list) {
            RecordsVo vo = RecordsVo.builder()
                    .id(records.getId())
                    .buyerId(records.getBuyer().getId())
                    .sellerId(records.getSeller().getId())
                    .goodID(records.getGoods().getId())
                    .transactionTime(records.getTransactionTime())
                    .tradingLocation(records.getTradingLocation())
                    .state(records.isState())
                    .price(records.getPrice())
                    .build();
            recordsVos.add(vo);
        }
        return recordsVos;
    }

    @DeleteMapping("/deleteRecord")
    public int deleteRecord(@RequestParam(name = "ID") String id){
        transactionRecordsService.delete(id);
        return 200;
    }

}
