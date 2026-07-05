package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.PurchaseDto;
import online.mushu.server.Entity.PurchaseInf;
import online.mushu.server.Entity.User;
import online.mushu.server.Service.PurchaseInfService;
import online.mushu.server.Service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * =======
 * =======
 */

@RequestMapping("/api/purchase")
public class PurchaseController {

    @Resource
    PurchaseInfService purchaseInfService;
    @Resource
    UserService userService;

    @PostMapping("/uploadPurchase")
    public String upload(@RequestBody PurchaseDto dto) {
        String id = UUID.randomUUID().toString();
        User user = userService.getUserById(dto.getUserID());
        PurchaseInf purchaseInf = new PurchaseInf(id, user, dto.getGoodsName(), dto.getType(), dto.getText(), dto.getPurchaseTime(), dto.getPrice());
        purchaseInfService.savePurchase(purchaseInf);
        return "success";
    }

    @PostMapping("/changePurchase")
    public String changePurchase(@RequestBody PurchaseDto dto, @RequestParam(name = "ID") String id) {
        PurchaseInf purchaseInf = purchaseInfService.getPurchaseById(id);
        purchaseInf.setGoodsName(dto.getGoodsName());
        purchaseInf.setType(dto.getType());
        purchaseInf.setText(dto.getText());
        purchaseInf.setPrice(dto.getPrice());
        purchaseInf.setPurchaseTime(dto.getPurchaseTime());
        purchaseInfService.updatePurchase(purchaseInf);
        return "success";
    }

    @GetMapping("/getPurchaseByType")
    public List<PurchaseInf> getPurchaseByType(@RequestParam(name = "type") int type) {
        return purchaseInfService.getByType(type);
    }

    @GetMapping("/getPurchaseByUserID")
    public List<PurchaseInf> getPurchaseByUserID(@RequestParam(name = "userID") int userID) {
        return purchaseInfService.getByUserID(userID);
    }

    @GetMapping("/getPurchaseByID")
    public PurchaseInf getPurchaseByID(@RequestParam(name = "id") String id) {
        return purchaseInfService.getPurchaseById(id);
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam(name = "id") String id) {
        purchaseInfService.deletePurchase(id);
        return "success";
    }
}
