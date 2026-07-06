package online.mushu.server.Repository;


import online.mushu.server.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * =======
 * =======
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    UserProfile findByID(int id);



}
