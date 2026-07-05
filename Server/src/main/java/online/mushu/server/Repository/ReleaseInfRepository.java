package online.mushu.server.Repository;

import online.mushu.server.Entity.ReleaseInf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * =======
 * =======
 */
public interface ReleaseInfRepository extends JpaRepository<ReleaseInf, String> {

    List<ReleaseInf> findByHurryMarkTrue();

    List<ReleaseInf> findByTopMarkTrue();

    ReleaseInf findByID(String id);

    ReleaseInf updateByID(String id, ReleaseInf entity);
}
