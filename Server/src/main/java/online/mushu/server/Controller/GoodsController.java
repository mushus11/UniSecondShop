package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.GetGoodsInfDto;
import online.mushu.server.Dto.GoodDto;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Entity.User;
import online.mushu.server.Service.GoodsService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.GetGoodsInfVo;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * =======
 * =======
 */
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private UserService userService;

    @PostMapping("/uploadGoodsInf")
    public String uploadGoodsInf(@RequestBody List<GoodDto> dtoList) {
        List<Goods> goods = new ArrayList<>();
        for (GoodDto dto : dtoList) {
            String id = UUID.randomUUID().toString();
            User user = userService.getUserById(dto.getUserId());
            Goods good = new Goods(id, user, dto.getName(), dto.getType(), dto.getPrice(), dto.getText(), dto.isState());
            goods.add(good);
        }
        goodsService.saveAll(goods);
        return "success";
    }

    @PostMapping("/changeGoodInf")
    public String changeGoodInf(@RequestBody GoodDto dto, @RequestParam(name = "goodID") String goodID) {
        User user = userService.getUserById(dto.getUserId());
        Goods good = new Goods(goodID, user, dto.getName(), dto.getType(), dto.getPrice(), dto.getText(), dto.isState());
        goodsService.updateGood(good);
        return "success";
    }

//    查找单个商品
    @GetMapping("/getGoodInf")
    public GetGoodsInfVo getGoodInf(@RequestBody GetGoodsInfDto dto) {
        String id = dto.getGoodID();
        Goods good = goodsService.getGoodsByID(id);
        return GetGoodsInfVo.builder()
                .id(good.getID())
                .userId(good.getUser().getID())
                .name(good.getName())
                .price(good.getPrice())
                .text(good.getText())
                .type(good.getType())
                .state(good.isState())
                .build();

    }

//    查找所有商品
    @GetMapping("/getGoodsInf")
    public List<GetGoodsInfVo> getGoodsInf(@RequestBody GetGoodsInfDto dto) {
        int id = dto.getUserID();
        List<GetGoodsInfVo> goodsInfVos = new ArrayList<>();
        List<Goods> goods = goodsService.getGoods(id);
        for (Goods good : goods) {
            GetGoodsInfVo getGoodsInfVo = GetGoodsInfVo.builder()
                    .id(good.getID())
                    .userId(good.getUser().getID())
                    .name(good.getName())
                    .price(good.getPrice())
                    .text(good.getText())
                    .type(good.getType())
                    .state(good.isState())
                    .build();
            goodsInfVos.add(getGoodsInfVo);
        }
        return goodsInfVos;
    }

}
