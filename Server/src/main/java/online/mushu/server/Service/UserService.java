package online.mushu.server.Service;


import online.mushu.server.Entity.User;
import online.mushu.server.Repository.UserRepository;
import online.mushu.server.Security.UserIdDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * =======
 * =======
 */

@Component
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = getByName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }

        return UserIdDetail.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(null)
                .build();
    }

    public UserDetails loadUserById(int id) {

        User user = getUserById(id);
        if (user == null) {
            throw new UsernameNotFoundException(String.valueOf(id));
        }

        return UserIdDetail.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(null)
                .build();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User getByName(String name) {
        return userRepository.findByUsername(name);
    }

    public User getUserById(int id) {
        return userRepository.findById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
