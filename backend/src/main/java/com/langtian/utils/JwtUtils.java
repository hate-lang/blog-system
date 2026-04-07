package com.langtian.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtils {
    // 1. 绝密防伪印章（密码学要求必须大于 32 个字符，千万别泄露给别人）
    private static final String SIGN_KEY = "MyBlogSuperSecretKeyForJwtGeneration1234567890";

    // 把字符串变成加密算法需要的密钥对象
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SIGN_KEY.getBytes(StandardCharsets.UTF_8));

    // 2. 通行证有效时间：设置为 24 小时（单位是毫秒）
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24;

    /**
     * 颁发通行证 (生成 JWT)
     *
     * @param claims 你想存进通行证里的信息（比如 userId, username）
     * @return 返回那串长长的乱码
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims) // 塞入用户信息
                .expiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 设置过期时间
                .signWith(KEY) // 盖上防伪印章
                .compact(); // 压缩打包！
    }

    /**
     * 查验通行证 (解析 JWT)
     * 如果通行证是假的，或者过期了，这里会直接抛出报错！
     */
    public static Map<String, Object> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY) // 用印章验真伪
                .build()
                .parseSignedClaims(token) // 拆开包裹
                .getPayload(); // 拿出里面的用户信息
    }
}
