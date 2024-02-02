package com.ssafy.relpl.util.jwt;

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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public static String createAccessToken(JwtTokenProvider jwtTokenProvider, Long userId) {
        Claims claims = Jwts.claims();
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtTokenProvider.accessExpirationTime);
        claims.put("userId", String.valueOf(userId));
        claims.put("role", List.of("USER"));

        Key key = new SecretKeySpec(jwtTokenProvider.secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());

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
    public String createRefreshToken(JwtTokenProvider jwtTokenProvider, String userId){
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtTokenProvider.refreshExpirationTime);
        Key key = new SecretKeySpec(jwtTokenProvider.secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();

        // redis에 저장
        redisTemplate.opsForValue().set(
                "token_"+userId,
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
        Key key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        String username = (String) Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("userId");
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

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
            Key key = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
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


}
