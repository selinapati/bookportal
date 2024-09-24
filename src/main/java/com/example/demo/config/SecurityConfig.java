package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll() // Permit all requests
            )
            .formLogin(form -> form
                .loginPage("/login") // Custom login page
                .failureUrl("/login?error=true") // Redirect to login page with error on failure
                .defaultSuccessUrl("/bookBorrow", true) // Redirect to bookBorrow on success
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity

        return http.build();
    }

    // Add the PasswordEncoder bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
