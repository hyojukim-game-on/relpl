package com.ssafy.relpl.util.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // HTTP 요청에서 JWT 토큰 추출
            String token = jwtTokenProvider.resolveToken(request);

            // JWT 토큰이 존재하고 유효한 경우
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰에서 인증 객체 추출
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                // SecurityContext에 인증 객체 설정
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.error("JWT 처리 중 오류 발생..: {}", e.getMessage());
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
