package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.PurchaseDto;
import online.mushu.server.Entity.PurchaseInf;
import online.mushu.server.Entity.User;
import online.mushu.server.Service.PurchaseInfService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.PurchaseInfVo;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * =======
 * =======
 */
@RestController
@RequestMapping("/api/Purchase")
public class PurchaseController {

    @Resource
    PurchaseInfService purchaseInfService;
    @Resource
    UserService userService;

    @PostMapping("/uploadPurchase")
    public int upload(@RequestBody PurchaseDto dto) {
        String id = UUID.randomUUID().toString();
        User user = userService.getUserById(dto.getUserID());
        Timestamp purchaseTime = new Timestamp(System.currentTimeMillis());
        PurchaseInf purchaseInf = new PurchaseInf(id, user, dto.getGoodsName(), dto.getType(), dto.getText(), purchaseTime, dto.getPrice());
        purchaseInfService.savePurchase(purchaseInf);
        return 200;
    }

    @PostMapping("/changePurchase")
    public int changePurchase(@RequestBody PurchaseDto dto, @RequestParam(name = "ID") String id) {
        PurchaseInf purchaseInf = purchaseInfService.getPurchaseById(id);
        purchaseInf.setGoodsName(dto.getGoodsName());
        purchaseInf.setType(dto.getType());
        purchaseInf.setText(dto.getText());
        purchaseInf.setPrice(dto.getPrice());
        purchaseInf.setPurchaseTime(new Timestamp(System.currentTimeMillis()));
        purchaseInfService.updatePurchase(purchaseInf);
        return 200;
    }

    @GetMapping("/getPurchaseByType")
    public List<PurchaseInfVo> getPurchaseByType(@RequestParam(name = "type") int type) {
        List<PurchaseInf> infs =  purchaseInfService.getByType(type);
        List<PurchaseInfVo> vos = new ArrayList<>();
        for (PurchaseInf inf : infs) {
            PurchaseInfVo vo = PurchaseInfVo.builder()
                    .id(inf.getId())
                    .userId(inf.getUser().getId())
                    .type(inf.getType())
                    .goodName(inf.getGoodsName())
                    .text(inf.getText())
                    .purchaseTime(inf.getPurchaseTime())
                    .price(inf.getPrice())
                    .build();
            vos.add(vo);
        }
        return vos;
    }

    @GetMapping("/getPurchaseByUserID")
    public List<PurchaseInfVo> getPurchaseByUserID(@RequestParam(name = "userID") int userID) {
        List<PurchaseInf> infs = purchaseInfService.getByUserID(userID);
        List<PurchaseInfVo> vos = new ArrayList<>();
        for (PurchaseInf inf : infs) {
            PurchaseInfVo vo = PurchaseInfVo.builder()
                    .id(inf.getId())
                    .userId(userID)
                    .type(inf.getType())
                    .goodName(inf.getGoodsName())
                    .text(inf.getText())
                    .purchaseTime(inf.getPurchaseTime())
                    .price(inf.getPrice())
                    .build();
            vos.add(vo);
        }
        return vos;
    }

    @GetMapping("/getPurchaseByID")
    public PurchaseInfVo getPurchaseByID(@RequestParam(name = "id") String id) {
        PurchaseInf inf = purchaseInfService.getPurchaseById(id);
        return PurchaseInfVo.builder()
                .id(inf.getId())
                .userId(inf.getUser().getId())
                .type(inf.getType())
                .goodName(inf.getGoodsName())
                .text(inf.getText())
                .purchaseTime(inf.getPurchaseTime())
                .price(inf.getPrice())
                .build();
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam(name = "id") String id) {
        purchaseInfService.deletePurchase(id);
        return "success";
    }
}
