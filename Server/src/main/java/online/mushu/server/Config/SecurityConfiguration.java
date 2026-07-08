package online.mushu.server.Config;


import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import online.mushu.server.Common.Util.JWTUtils;
import online.mushu.server.Entity.UserProfile;
import online.mushu.server.Filter.JSONUsernamePasswordFilter;
import online.mushu.server.Filter.JWTAuthorizeFilter;
import online.mushu.server.Security.UserIDAuthenticationToken;
import online.mushu.server.Security.UserIDProvider;
import online.mushu.server.Security.UserIdDetail;
import online.mushu.server.Service.UserProfileService;
import online.mushu.server.Service.UserService;
import online.mushu.server.Vo.Login;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * =======
 * =======
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Resource
    private JWTUtils jwtUtils;

    @Resource
    private JWTAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    private UserService userService;

    @Resource
    private UserProfileService userProfileService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserIDProvider userIDProvider() {
        return new UserIDProvider();
    }


    @Bean
    public JSONUsernamePasswordFilter jsonUsernamePasswordFilter(AuthenticationManager authManager) {
        JSONUsernamePasswordFilter filter = new JSONUsernamePasswordFilter(authManager);
        filter.setAuthenticationSuccessHandler(this::onAuthenticationSuccess);
        filter.setAuthenticationFailureHandler(this::onAuthenticationFailure);
        filter.setFilterProcessesUrl("/api/user/login");
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return http

//                除登陆意外所有请求都需验证
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/", "/favicon.ico", "/static/*").permitAll()
                        .requestMatchers("/api/user/login").permitAll()//表示将所有 /api/auth/login 到来的数据都放行
                        .requestMatchers("/api/user/register", "/api/user/changePassword").permitAll()//表示注册的路径放行
                        .requestMatchers("/api/image/**").permitAll()//图片资源无需认证
                        .anyRequest().authenticated()
                )

                .csrf(AbstractHttpConfigurer:: disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .userDetailsService(userService)
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(userIDProvider())
                .addFilterAt(jsonUsernamePasswordFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class)

                .build(); //其他URL到来的数据都需要验证)

    }


    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException
    {
        System.out.println("Login Success");//测试使用
//        取出用户信息
        UserIDAuthenticationToken token = (UserIDAuthenticationToken) authentication;
        UserIdDetail user = token.getUserDetails();
//        创建JWT令牌
        String jwt = jwtUtils.creatJWT(user);

//        将令牌发放给前端
        response.setContentType("application/json; charset=utf-8");

        UserProfile profile = userProfileService.getUserProfile(user.getId());
        int access = (profile != null) ? profile.getID() : 0;

        response.getWriter().write(Login.Success(user.getUsername(), access, user.getId(), jwt).asJsonString());
    }

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException
    {
        System.out.println("Login Failure");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(Login.Failure().asJsonString());
    }


}
