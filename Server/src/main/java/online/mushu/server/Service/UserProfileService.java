package online.mushu.server.Service;


import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Repository.UserProfileRepository;
import org.springframework.stereotype.Service;

/**
 * =======
 * =======
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public UserProfile getUserProfile(int id) {
        return userProfileRepository.findByID(id);
    }

    public UserProfile saveUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
