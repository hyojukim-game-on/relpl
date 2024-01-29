package com.ssafy.relpl.util;

import com.ssafy.relpl.service.UserDetailsServiceImpl;
import com.ssafy.relpl.util.exception.BaseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

//    @Value("${jwt.token.secret}")
//    private static String key;
    private static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${jwt.token.access-expiration-time}")
    private static long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public static String createAccessToken(Authentication authentication) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);
        claims.put("userNickname", authentication.getName());
        claims.put("role", List.of("USER"));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * Refresh 토큰 생성
     */
    public String createRefreshToken(Authentication authentication){
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        // redis에 저장
        redisTemplate.opsForValue().set(
                authentication.getName(),
                refreshToken,
                refreshExpirationTime,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    /**
     * 토큰으로부터 클레임을 만들고, 이를 통해 User 객체 생성해 Authentication 객체 반환
     */
    public Authentication getAuthentication(String token) {
        String userPrincipal = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * http 헤더로부터 bearer 토큰을 가져옴.
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Access 토큰을 검증
     */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e) {
            log.error(JwtConstants.EXPIRED_JWT_MESSAGE);
            throw new BaseException(JwtConstants.EXPIRED_JWT_MESSAGE);
        } catch(JwtException e) {
            log.error(JwtConstants.INVALID_JWT_MESSAGE);
            throw new BaseException(JwtConstants.INVALID_JWT_MESSAGE);
        }
    }

//    public static boolean isExpired(String token, String key) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
//                .getBody().getExpiration().before(new Date());
//    }
//
//    public static String getUserNickname(String token, String key) {
//        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
//                .getBody().get("userNickname", String.class);
//    }
//
//    public static List<String> getRole(String token, String key) {
//        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
//        return (List<String>) claims.get("role");
//    }
}
