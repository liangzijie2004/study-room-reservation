package com.campus.studyroom.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.campus.studyroom.common.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-hours:24}")
    private long expireHours;

    public String createToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + expireHours * 60 * 60 * 1000);
        return JWT.create()
                .withIssuer("study-room")
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 校验并解析 token，失败抛出 401 业务异常
     */
    public LoginUser parse(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer("study-room")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return new LoginUser(
                    jwt.getClaim("userId").asLong(),
                    jwt.getClaim("username").asString(),
                    jwt.getClaim("role").asString());
        } catch (JWTVerificationException e) {
            throw new BusinessException(401, "登录已过期，请重新登录");
        }
    }
}