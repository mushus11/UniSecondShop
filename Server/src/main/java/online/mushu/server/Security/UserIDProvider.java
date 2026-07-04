package online.mushu.server.Security;


import jakarta.annotation.Resource;
import online.mushu.server.Service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * =======
 * =======
 */
public class UserIDProvider implements AuthenticationProvider {

    @Resource
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserIDAuthenticationToken token = (UserIDAuthenticationToken) authentication;
        UserIdDetail detail;

        try {
            detail = (UserIdDetail) userService.loadUserById((Integer) token.getPrincipal());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid username");
        }
        String password = detail.getPassword();
        if (password.equals(token.getCredentials())) {
            return UserIDAuthenticationToken.authenticated(detail);
        } else {
            throw new BadCredentialsException("Invalid password");
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UserIDAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
