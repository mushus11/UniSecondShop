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
        List<Goods> goodsList = new ArrayList<Goods>();
        goodsList = goodsRepository.findByUserID(id);
        return goodsList;
    }

    public void saveAll(List<Goods> goodsList) {
        goodsRepository.saveAll(goodsList);
    }

    public void save(Goods goods) {
        goodsRepository.save(goods);
    }

    public Goods getGoodsByID(String id) {
        return goodsRepository.findByID(id);
    }

    public List<Goods> getGoodsByType(int type) {
        return goodsRepository.findByType(type);
    }

    public void updateGood(Goods good) {
        String id = good.getID();
        goodsRepository.updateByID(id, good);
    }

    public void deleteGood(String id) {
        goodsRepository.deleteByID(id);
    }
}
