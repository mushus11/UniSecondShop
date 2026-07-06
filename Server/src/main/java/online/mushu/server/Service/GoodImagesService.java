package online.mushu.server.Service;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import online.mushu.server.Entity.GoodImages;
import online.mushu.server.Repository.GoodImagesRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * =======
 * =======
 */
@Service
public class GoodImagesService {

    GoodImagesRepository goodImagesRepository;
    public GoodImagesService(GoodImagesRepository goodImagesRepository) {
        this.goodImagesRepository = goodImagesRepository;
    }

    public void saveImage(GoodImages goodImages) {
        goodImagesRepository.save(goodImages);
    }

    @Transactional
    public void deleteImage(String id) {
        goodImagesRepository.deleteByGoodID(id);
    }

    public List<GoodImages> getImageID(String goodID) {
        return goodImagesRepository.findByGoodID(goodID);
    }

    public GoodImages getImage(String id) {
        return goodImagesRepository.findById(id).orElse(null);
    }
}
