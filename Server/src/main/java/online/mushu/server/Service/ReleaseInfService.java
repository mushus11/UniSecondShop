package online.mushu.server.Service;

import online.mushu.server.Entity.ReleaseInf;
import online.mushu.server.Repository.ReleaseInfRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =======
 * =======
 */
@Service
public class ReleaseInfService {

    ReleaseInfRepository releaseInfRepository;
    public ReleaseInfService(ReleaseInfRepository releaseInfRepository) {
        this.releaseInfRepository = releaseInfRepository;
    }

    public List<ReleaseInf> getHurry() {
        return releaseInfRepository.findByHurryMarkTrue();
    }

    public List<ReleaseInf> getTop() {
        return releaseInfRepository.findByTopMarkTrue();
    }

    public void saveReleaseInf(ReleaseInf releaseInf) {
        releaseInfRepository.save(releaseInf);
    }

    public ReleaseInf getReleaseInf(String id) {
        return releaseInfRepository.findByID(id);
    }

    public void updateReleaseInf(ReleaseInf releaseInf) {
        releaseInfRepository.updateByID(releaseInf.getID(), releaseInf);
    }

    public void deleteReleaseInf(String id) {}


}
