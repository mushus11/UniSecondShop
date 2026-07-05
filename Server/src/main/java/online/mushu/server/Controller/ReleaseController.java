package online.mushu.server.Controller;

import jakarta.annotation.Resource;
import online.mushu.server.Entity.ReleaseInf;
import online.mushu.server.Service.ReleaseInfService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;

/**
 * =======
 * =======
 */
@RequestMapping("/api/Release")
public class ReleaseController {

    @Resource
    ReleaseInfService releaseInfService;

    @PostMapping("/changeStateUp")
    public String changeStateUp(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(1);
        releaseInfService.updateReleaseInf(releaseInf);
        return "success";
    }

    @PostMapping("/changeStateDown")
    public String changeStateDown(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(2);
        releaseInf.setDownLoadTime(new Timestamp(System.currentTimeMillis()));
        releaseInfService.updateReleaseInf(releaseInf);
        return "success";
    }

    @PostMapping("/changeStateOut")
    public String changeStateOut(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setState(3);
        releaseInfService.updateReleaseInf(releaseInf);
        return "success";
    }

    @PostMapping("/changeHurry")
    public String changeHurry(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setHurryMark(!releaseInf.isHurryMark());
        releaseInfService.updateReleaseInf(releaseInf);
        return "success";
    }
    @PostMapping("/changeTop")
    public String changeTop(@RequestParam(name = "ID") String id) {
        ReleaseInf releaseInf = releaseInfService.getReleaseInf(id);
        releaseInf.setTopMark(!releaseInf.isTopMark());
        releaseInfService.updateReleaseInf(releaseInf);
        return "success";
    }


}
