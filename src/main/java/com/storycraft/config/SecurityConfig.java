package com.storycraft.config;

import com.storycraft.auth.jwt.JwtConfigurer;
import com.storycraft.auth.jwt.JwtTokenProvider;
import com.storycraft.auth.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/swagger-api-docs",
                                "/webjars/**",
                                "/auth/login",
                                "/auth/signup",
                                "/auth/request-reset-code",
                                "/auth/verify-reset-code",
                                "/auth/reset-password",
                                "/email/verification/exists",
                                "/auth/token/refresh",
                                "/nickname/exists",
                                "/ai/**",                   //추후 삭제
                                "/story/**",
                                "/quiz/**",
                                "/speech/**",
                                "/illustration",
                                "/integration"              //여기까지
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .apply(new JwtConfigurer(jwtTokenProvider, customUserDetailsService));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}


