package online.mushu.server.Security;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

/**
 * =======
 * =======
 */
public class UserIDAuthenticationToken extends AbstractAuthenticationToken {
    @Serial
    private static final long serialVersionUID = 225L;
    @Setter
    private int principal;
    @Setter
    @Getter
    private String credentials = "";
    @Setter
    @Getter
    private UserIdDetail userDetails;


    public UserIDAuthenticationToken(int principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setPrincipal(principal);
        setCredentials(credentials);
        setAuthenticated(false);
    }

    public UserIDAuthenticationToken(UserIdDetail detail, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setPrincipal(userDetails.getId());
        setCredentials(null);
        setDetails(detail);
        setAuthenticated(true);
    }


    public static UserIDAuthenticationToken unauthenticated(int principal, String credentials) {
        return new UserIDAuthenticationToken(principal, credentials, null);
    }

    public static UserIDAuthenticationToken authenticated(UserIdDetail detail) {
        return new UserIDAuthenticationToken(detail, null);
    }


    @Override
    public @Nullable Object getPrincipal() {
        return this.principal;
    }
}
