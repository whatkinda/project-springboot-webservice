package com.birdcft.springboot.web.config;

import com.birdcft.springboot.web.config.oauth.PrincipalOAuth2UserService;
import com.birdcft.springboot.web.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final PrincipalOAuth2UserService principalOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {
        return http
                .csrf(csrfConfig -> csrfConfig
                        .disable())

                .headers(headersConfig -> headersConfig
                        .frameOptions(option -> option.disable()))

                .authorizeHttpRequests(req -> req
                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                        .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                        .anyRequest().authenticated())

                .logout(logout -> logout
                        .logoutSuccessUrl("/"))

                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(config -> config.userService(principalOAuth2UserService)))

                .build();
    }
}
