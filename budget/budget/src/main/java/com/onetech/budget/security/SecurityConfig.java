package com.onetech.budget.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String ADMIN = "admin";
    public static final String USER = "user";
    private final JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers(HttpMethod.POST, "/api/categories").permitAll()
                        // .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                        //.requestMatchers(HttpMethod.POST, "/api/categories").hasRole(USER)
                        .requestMatchers("/api/categorie").hasRole(USER)

                        //    .requestMatchers(HttpMethod.DELETE, "/api/categories").hasRole(USER)

                        //.requestMatchers(HttpMethod.GET, "/api/user/**").hasRole(USER)
                        //.requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole(ADMIN)
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
                .build();
    }
}