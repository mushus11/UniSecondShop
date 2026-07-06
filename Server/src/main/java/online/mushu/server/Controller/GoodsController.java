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
import online.mushu.server.Vo.ReleaseInfVo;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

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
    public int changeGoodInf(@RequestBody GoodDto dto, @RequestParam(name = "goodID") String goodID) {
        User user = userService.getUserById(dto.getUserId());
        Goods good = new Goods(goodID, user, dto.getName(), dto.getType(), dto.getPrice(), dto.getText(), dto.isState());
        goodsService.updateGood(good);
        return 200;
    }

    @PostMapping("/sellGood")
    public int sellGood(@RequestParam(name = "goodID") String id) {
        Goods good = goodsService.getGoodsByID(id);
        good.setState(true);
        goodsService.updateGood(good);
        ReleaseInf inf = releaseInfService.getReleaseInf(id);
        inf.setState(3);
        releaseInfService.saveReleaseInf(inf);
        return 200;
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
        List<Goods> goods = goodsService.getGoodsByType(type);
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
    public List<ReleaseInfVo> getGoodsInfIsHarry() {
        System.out.println("aaaaaaaaaaaa");
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getHurry();
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .GoodsID(inf.getGoods().getId())
                    .downLoadTime(inf.getDownLoadTime())
                    .hurryMark(inf.isHurryMark())
                    .topMark(inf.isTopMark())
                    .upLoadTime(inf.getUpLoadTime())
                    .state(inf.getState())
                    .build();
            list.add(vo);
        }
        return list;
    }

    @GetMapping("/getAllGoods")
    public List<ReleaseInfVo> getAllGoods() {
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getAll();
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .GoodsID(inf.getGoods().getId())
                    .downLoadTime(inf.getDownLoadTime())
                    .hurryMark(inf.isHurryMark())
                    .topMark(inf.isTopMark())
                    .upLoadTime(inf.getUpLoadTime())
                    .state(inf.getState())
                    .build();
            list.add(vo);
        }
        return list;
    }

    @GetMapping("/getGoodsInfIsTop")
    public List<ReleaseInfVo> getGoodsInfIsTop() {
        System.out.println("aaaaaaaaaaaa");
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getTop();
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .GoodsID(inf.getGoods().getId())
                    .downLoadTime(inf.getDownLoadTime())
                    .hurryMark(inf.isHurryMark())
                    .topMark(inf.isTopMark())
                    .upLoadTime(inf.getUpLoadTime())
                    .state(inf.getState())
                    .build();
            list.add(vo);
        }
        return list;
    }

    @GetMapping("/getGoodNumByType")
    public Map<Integer, Integer> getGoodNumByType() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 6; i++) {
            int num = goodsService.typeName(i);
            map.put(i, num);
        }
        return map;
    }

    @DeleteMapping("/delete")
    public int delete(@RequestParam(name = "goodID") String goodID) {
        goodsService.deleteGood(goodID);
        return 200;
    }

}
