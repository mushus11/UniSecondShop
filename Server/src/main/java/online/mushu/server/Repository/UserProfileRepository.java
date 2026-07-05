package online.mushu.server.Repository;


import online.mushu.server.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * =======
 * =======
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    UserProfile findByID(int id);

}
