package com.interbank.horizonte.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.DispatcherType;

@Configuration
@SecurityScheme(name = "banco", type = SecuritySchemeType.HTTP, scheme = "basic")
class SecurityConfig {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().hasRole("BANCO_KWANZA"))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    InMemoryUserDetailsManager interbankUser(@Value("${interbank.shared-password}") String secret) {
        if (!secret.matches("[!-~]{32,64}")) {
            throw new IllegalArgumentException("INTERBANK_SHARED_PASSWORD deve ter 32 a 64 caracteres ASCII sem espaços");
        }
        var passwords = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var user = User.withUsername("banco-kwanza").password(passwords.encode(secret)).roles("BANCO_KWANZA").build();
        return new InMemoryUserDetailsManager(user);
    }
}
