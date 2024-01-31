package com.ssafy.relpl.config;

import com.ssafy.relpl.util.jwt.JwtAccessDeniedHandler;
import com.ssafy.relpl.util.jwt.JwtAuthenticationEntryPoint;
import com.ssafy.relpl.util.jwt.JwtFilter;
import com.ssafy.relpl.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

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
                            .requestMatchers("/api/user/signup").permitAll() //회원가입 열어둠
                            .requestMatchers("/api/user/login").permitAll() //로그인 열어둠
                            .requestMatchers("/api/sample/post").permitAll() //로그인 열어둠
                            .requestMatchers("/api/sample/get/**").permitAll() //로그인 열어둠
                            .requestMatchers("/api/project/recommend").permitAll()
                            .anyRequest().authenticated(); //인증 필요
                    })
                .csrf((csrf) -> csrf.disable())
                .cors((c) -> c.disable())
                .headers((headers) -> headers.disable())
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler()))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
