package online.mushu.server.Security;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


/**
 * =======
 * =======
 */
@Data
@NoArgsConstructor
@Builder
public class UserIdDetail implements UserDetails {

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private int id;
    private boolean enabled;

    public UserIdDetail(String username, int id, String password, Collection<? extends GrantedAuthority> authorities) {
        this(username, password, authorities, id, true);
    }

    public UserIdDetail(String username, String password, Collection<? extends GrantedAuthority> authorities, int id, boolean enabled) {
        setUsername(username);
        setPassword(password);
        setAuthorities(authorities);
        setId(id);
        setEnabled(enabled);

    }

}

