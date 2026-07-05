package online.mushu.server.Repository;

import online.mushu.server.Entity.GoodImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * =======
 * =======
 */
public interface GoodImagesRepository extends JpaRepository<GoodImages, String> {

    @Modifying
    @Query("delete from GoodImages gi where gi.goods.id = :goodID")
    void deleteByGoodID(@Param("goodID") String goodID);

}
