package online.mushu.server.Repository;

import online.mushu.server.Entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * =======
 * =======
 */
public interface GoodsRepository extends JpaRepository<Goods, String> {

    List<Goods> findByUserID(int id);

    Goods findByID(String id);

    Goods updateByID(String id, Goods goods);

    List<Goods> findByType(int type);

    Goods deleteByID(String id);

}
