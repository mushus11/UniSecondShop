package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.GetGoodsInfDto;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Service.GoodsService;
import online.mushu.server.Vo.GetGoodsInfVo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * =======
 * =======
 */
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @PostMapping("/uploadGoodsInf")
    public String uploadGoodsInf(@RequestBody List<Goods> goods) {
        goodsService.saveAll(goods);
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
