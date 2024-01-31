package com.ssafy.relpl.util.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.relpl.util.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final ExceptionResponseHandler exceptionResponseHandler;

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
        } catch (BaseException e) {
            ResponseEntity<String> responseEntity = exceptionResponseHandler.handleBaseException(e);
            response.setStatus(HttpStatus.GONE.value());
            response.setContentType("application/json");
            response.getWriter().write(responseEntity.getBody());
            return;
        } catch (Exception e) {
            log.info("jwt error : " + e);
        }
        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}
