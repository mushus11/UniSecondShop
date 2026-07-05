package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Dto.GetGoodsInfDto;
import online.mushu.server.Dto.GoodDto;
import online.mushu.server.Entity.Goods;
import online.mushu.server.Entity.ReleaseInf;
import online.mushu.server.Entity.User;
import online.mushu.server.Service.GoodsService;
import online.mushu.server.Service.ReleaseInfService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.GetGoodsInfVo;
import online.mushu.server.Vo.GoodVo;
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
@RequestMapping("/api/goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private UserService userService;
    @Resource
    private ReleaseInfService releaseInfService;

//    可行
    @PostMapping("/uploadGoodInf")
    public GoodVo uploadGoodInf(@RequestBody GoodDto dto) {
        String id = UUID.randomUUID().toString();
        User user = userService.getUserById(dto.getUserId());
        Goods good = new Goods(id, user, dto.getName(), dto.getType(), dto.getPrice(), dto.getText(), dto.isState());
        goodsService.save(good);
        Timestamp upLoadTime = new Timestamp(System.currentTimeMillis());
        ReleaseInf releaseInf = new ReleaseInf(id, good, upLoadTime, null, 0, false, false);
        releaseInfService.saveReleaseInf(releaseInf);
        return GoodVo.builder()
                .goodID(id)
                .userID(dto.getUserId())
                .build();
    }

//    可行
    @PostMapping("/changeGoodInf")
    public String changeGoodInf(@RequestBody GoodDto dto, @RequestParam(name = "goodID") String goodID) {
        User user = userService.getUserById(dto.getUserId());
        Goods good = new Goods(goodID, user, dto.getName(), dto.getType(), dto.getPrice(), dto.getText(), dto.isState());
        goodsService.updateGood(good);
        return "success";
    }

//    可行
//    查找单个商品
    @GetMapping("/getGoodInf")
    public GetGoodsInfVo getGoodInf(@RequestBody GetGoodsInfDto dto) {
        String id = dto.getGoodID();
        Goods good = goodsService.getGoodsByID(id);
        return GetGoodsInfVo.builder()
                .id(good.getId())
                .userId(good.getUser().getId())
                .name(good.getName())
                .price(good.getPrice())
                .text(good.getText())
                .type(good.getType())
                .state(good.isState())
                .build();

    }

//    可行
//    查找所有商品
    @GetMapping("/getGoodsInfByUser")
    public List<GetGoodsInfVo> getGoodsInfByUser(@RequestBody GetGoodsInfDto dto) {
        int id = dto.getUserID();
        List<GetGoodsInfVo> goodsInfVos = new ArrayList<>();
        List<Goods> goods = goodsService.getGoods(id);
        for (Goods good : goods) {
            GetGoodsInfVo getGoodsInfVo = GetGoodsInfVo.builder()
                    .id(good.getId())
                    .userId(good.getUser().getId())
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

//    可行
    @GetMapping("/getGoodsInfByType")
    public List<GetGoodsInfVo> getGoodsInfByType(@RequestBody GetGoodsInfDto dto) {
        int type = dto.getType();
        List<GetGoodsInfVo> list = new ArrayList<>();
        List<Goods> goods = goodsService.getGoods(type);
        for (Goods good : goods) {
            GetGoodsInfVo vo = GetGoodsInfVo.builder()
                    .id(good.getId())
                    .userId(good.getUser().getId())
                    .name(good.getName())
                    .price(good.getPrice())
                    .text(good.getText())
                    .type(good.getType())
                    .state(good.isState())
                    .build();
            list.add(vo);
        }
        return list;
    }

    @GetMapping("/getGoodsInfIsHarry")
    public List<ReleaseInf> getGoodsInfIsHarry() {
        return releaseInfService.getHurry();
    }

    @GetMapping("/getGoodsInfIsTop")
    public List<ReleaseInf> getGoodsInfIsTop() {
        return releaseInfService.getTop();
    }

    @DeleteMapping("/delete")
    public String delete(@RequestParam(name = "goodID") String goodID) {
        goodsService.deleteGood(goodID);
        return "success";
    }

}
