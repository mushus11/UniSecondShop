package online.mushu.server.Common.Util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.Resource;
import online.mushu.server.Common.Enums.Cost;
import online.mushu.server.Security.UserIdDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * ======= JWT工具类
 * ======= JWT的相关工具
 */

@Component
public class JWTUtils {

//    @Value("${jwt.sign}")
    String sign = "123456";

    @Resource
    StringRedisTemplate stringRedisTemplate;
    // 用于连接Redis数据库


    public String creatJWT(UserIdDetail detail) {



        Algorithm algorithm = Algorithm.HMAC256(sign);

//        创建JWT令牌
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("id", detail.getId())
                .withClaim("username", detail.getUsername())
                .withExpiresAt(this.expiresAt())
                .sign(algorithm);
    }

//    验证JWT令牌
    public DecodedJWT resoleJWT(String headerToken) {
        String token = this.convertToken(headerToken);
//        jwt为空或者过期则返回null
        if (token == null || this.isExpired(token)) return null;
        String uuid = JWT.decode(token).getId();
//        jwt被拉黑则返回null
        if (this.isInvalid(uuid)) return null;
        Algorithm algorithm = Algorithm.HMAC256(sign);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

//    移除JWT令牌
    public boolean invalidateJWT(String headerToken) {
        DecodedJWT jwt = this.resoleJWT(headerToken);
        if (jwt == null) return false;
        String token = jwt.getToken();
        return this.deleteJWT(token);
    }

//    取出JWT令牌中的用户信息
    public UserDetails toUser(DecodedJWT JWT) {
        Map<String, Claim> claims = JWT.getClaims();
        return UserIdDetail.builder()
                .id(claims.get("id").asInt())
                .username(claims.get("username").asString())
                .build();
    }

//    产生到期时间
    private Date expiresAt() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 7 * 24);
        return calendar.getTime();
    }

//    判断令牌是否到期
    private boolean isExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(new Date());
    }

//    取出请求头中包含的JWT令牌
    private String convertToken(String headerToken) {
        if (headerToken == null || !headerToken.startsWith("Bearer ")) return null;
        return headerToken.substring(7);
    }

//    判断JWT是否失效
    private boolean isInvalid(String uuid) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(Cost.JWT_BLACKLIST + uuid));
    }

//    拉黑JWT
    private boolean deleteJWT(String token) {
        String uuid = JWT.decode(token).getId();
        if (this.isInvalid(uuid)) return false;
        if (this.isExpired(token))
            stringRedisTemplate.opsForValue().set(Cost.JWT_BLACKLIST + uuid, "", 0, TimeUnit.MILLISECONDS);
        else {
            long time = JWT.decode(token).getExpiresAt().getTime() - new Date().getTime();
            stringRedisTemplate.opsForValue().set(Cost.JWT_BLACKLIST + uuid, "", time, TimeUnit.MILLISECONDS);
        }
        return true;
    }
}
