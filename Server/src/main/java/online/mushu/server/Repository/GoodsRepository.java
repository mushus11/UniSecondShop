package online.mushu.server.Repository;

import online.mushu.server.Entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * =======
 * =======
 */
public interface GoodsRepository extends JpaRepository<Goods, String> {

    @Modifying
    @Query("select g from Goods g where g.user.id = :id")
    List<Goods> findByUserID(@Param("id") int id);

    List<Goods> findByType(int type);


}
