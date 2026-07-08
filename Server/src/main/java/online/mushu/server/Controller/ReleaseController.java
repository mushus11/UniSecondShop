package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Entity.ReleaseInf;
import online.mushu.server.Service.ReleaseInfService;
import online.mushu.server.Vo.ReleaseInfVo;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * =======
 * =======
 */
@RestController
@RequestMapping("/api/Release")
public class ReleaseController {

    @Resource
    ReleaseInfService releaseInfService;

    @PostMapping("/changeStateUp")
    public int changeStateUp(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(1);
        releaseInfService.updateReleaseInf(releaseInf);
        return 200;
    }

    @PostMapping("/changeStateDown")
    public int changeStateDown(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(2);
        releaseInf.setDownLoadTime(new Timestamp(System.currentTimeMillis()));
        releaseInfService.updateReleaseInf(releaseInf);
        return 200;
    }

    @PostMapping("/changeStateOut")
    public int changeStateOut(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(3);
        releaseInfService.updateReleaseInf(releaseInf);
        return 200;
    }

    @PostMapping("/changeHurry")
    public int changeHurry(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setHurryMark(!releaseInf.isHurryMark());
        releaseInfService.updateReleaseInf(releaseInf);
        return 200;
    }
    @PostMapping("/changeTop")
    public int changeTop(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setTopMark(!releaseInf.isTopMark());
        releaseInfService.updateReleaseInf(releaseInf);
        return 200;
    }

    @GetMapping("/getGoodsState")
    public int getGoodsState(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        return releaseInf.getState();
    }

    @GetMapping("/getUpGoods")
    public List<ReleaseInfVo> getUpGoods() {
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getByState(1);
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .goodsID(inf.getGoods().getId())
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

    @GetMapping("/getDownGoods")
    public List<ReleaseInfVo> getDownGoods() {
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getByState(2);
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .goodsID(inf.getGoods().getId())
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

    @GetMapping("/getOutGoods")
    public List<ReleaseInfVo> getOutGoods() {
        List<ReleaseInfVo> list = new ArrayList<>();
        List<ReleaseInf> infs = releaseInfService.getByState(3);
        for (ReleaseInf inf : infs) {
            ReleaseInfVo vo = ReleaseInfVo.builder()
                    .id(inf.getId())
                    .goodsID(inf.getGoods().getId())
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
}
