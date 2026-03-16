package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình AuthenticationManager sử dụng AccountService
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cấu hình HttpSecurity
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/register").permitAll()
                .requestMatchers("/css/**", "/images/**", "/js/**").permitAll()
                .requestMatchers("/categories").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/categories/**").hasRole("ADMIN")
                .requestMatchers("/products").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        )
        .formLogin(form -> form.defaultSuccessUrl("/", true).permitAll())
        .logout(withDefaults());

        return http.build();
    }
}
