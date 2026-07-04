package online.mushu.server.Filter;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import online.mushu.server.Common.Util.JSONUtils;
import online.mushu.server.Dto.Login;
import online.mushu.server.Security.UserIDAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * =======过滤器
 * =======用于JSON格式数据的登陆验证，替换原有的表单验证
 */

public class JSONUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    private Boolean postOnly = true;

    public JSONUsernamePasswordFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException
    {
//        System.out.println("asdasdasd");
//        this.setPostOnly(false);
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else if (request.getParameter("username") != null && request.getParameter("password") != null) {
            return super.attemptAuthentication(request, response);
        } else {
            Login login;
            try {
                String JSON = JSONUtils.getJson(request.getReader());
                login = JSONUtils.fromJSON(JSON, Login.class);
            } catch (IOException e) {
                throw new AuthenticationServiceException(request.getRequestURI() + "传入参数损坏");
            } catch (NullPointerException e) {
                throw new AuthenticationServiceException(request.getRequestURI() + "传入参数错误");
            }

//            System.out.println(user.name() + user.password());


            UserIDAuthenticationToken token = UserIDAuthenticationToken.unauthenticated(login.getId(), login.getPassword());
            return this.getAuthenticationManager().authenticate(token);

        }
    }

    @Override
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
        super.setPostOnly(postOnly);
    }
}
