package online.mushu.server.Service;

import online.mushu.server.Entity.Goods;
import online.mushu.server.Repository.GoodsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * =======
 * =======
 */
@Service
public class GoodsService {

    private final GoodsRepository goodsRepository;

    public GoodsService(GoodsRepository goodsRepository) {
        this.goodsRepository = goodsRepository;
    }

    public List<Goods> getGoods(int id) {
        return goodsRepository.findByUserID(id);
    }

    public void saveAll(List<Goods> goodsList) {
        goodsRepository.saveAll(goodsList);
    }

    public void save(Goods goods) {
        goodsRepository.save(goods);
    }

    public Goods getGoodsByID(String id) {
        return goodsRepository.findById(id).orElse(null);
    }

    public List<Goods> getGoodsByType(int type) {
        return goodsRepository.findByType(type);
    }

    public void updateGood(Goods good) {
        goodsRepository.save(good);
    }

    public void deleteGood(String id) {
        goodsRepository.deleteById(id);
    }
}
