package com.ssafy.relpl.config;

import com.ssafy.relpl.util.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ExceptionResponseHandler exceptionHandler;
    private final JwtAuthenticationEntryPoint entryPoint;


    @Value("${jwt.token.secret}")
    private String key;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger UI 접근 허용
                            .requestMatchers("/api/user/signup").permitAll() //회원가입
                            .requestMatchers("/api/user/login").permitAll() //로그인
                            .requestMatchers("/api/user/token/reissue").permitAll() //토큰 재발급
                            .requestMatchers("/api/user/isExist/uid").permitAll() //유저 아이디 중복체크
                            .requestMatchers("/api/user/isExist/phone").permitAll() //유저 연락처 중복체크
                            .requestMatchers("/api/user/isExist/nickname/**").permitAll() //유저 닉네임 중복체크
                            .requestMatchers("/api/user/image").permitAll() //프로필 사진 등록
                            .requestMatchers("/api/sample/post").permitAll()
                            .requestMatchers("/api/sample/get/**").permitAll()
                            .anyRequest().authenticated(); //인증 필요
                })
                .csrf((csrf) -> csrf.disable())
                .cors((c) -> c.disable())
                .headers((headers) -> headers.disable())
                .addFilterBefore(new JwtFilter(jwtTokenProvider, exceptionHandler).disableAuth("/api/user/signup", "/api/user/login", "/api/user/token/reissue", "/api/user/isExist/uid", "/api/user/isExist/phone", "/api/user/isExist/nickname/**", "/api/user/image"), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(entryPoint) // ExceptionResponseHandler 사용
                        .accessDeniedHandler(new JwtAccessDeniedHandler()))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
