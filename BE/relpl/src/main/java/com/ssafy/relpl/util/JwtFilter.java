package com.ssafy.relpl.util;

import com.ssafy.relpl.util.exception.BaseException;
import io.jsonwebtoken.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth); // 정상 토큰이면 SecurityContext에 저장
            }
        } catch (RedisConnectionFailureException e) {
            SecurityContextHolder.clearContext();
            throw new BaseException(JwtConstants.REDIS_ERROR);
        } catch (Exception e) {
            throw new BaseException(JwtConstants.INVALID_JWT_MESSAGE);
        }

        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        final String authorization = request.getHeader("Authorization");
//        log.info("authorization : {}", authorization);
//
//        //token 이 없으면 BLOCK
//        if(authorization == null || !authorization.startsWith("Bearer ")) {
//            log.error("authorization 이 없습니다.");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        //token 이 만료되었다면 BLOCK
//        String token = authorization.split(" ")[1];
//        if(JwtTokenProvider.isExpired(token, key)) {
//            log.error("토큰이 만료되었습니다.");
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        //token에서 userId 꺼내기
//        String userNickname = JwtTokenProvider.getUserNickname(token, key);
//        log.info("userNickname : {}", userNickname);
//
//        //token에서 role List 꺼내기
//        List<String> role = JwtTokenProvider.getRole(token, key);
//
//        //권한 부여
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userNickname, null,
//                        role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
//
//        //Detail 넣기
//        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        filterChain.doFilter(request, response);
//    }
}
