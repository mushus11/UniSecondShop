package online.mushu.server.Repository;

import online.mushu.server.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * =======
 * =======
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByID(int id);

    User findByUsername(String username);


}
