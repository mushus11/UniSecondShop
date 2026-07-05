package online.mushu.server.Service;

import jakarta.annotation.Resource;
import online.mushu.server.Entity.GoodImages;
import online.mushu.server.Repository.GoodImagesRepository;
import org.springframework.stereotype.Service;

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

    public void deleteImage(String id) {
        goodImagesRepository.deleteByGoodID(id);
    }
}
