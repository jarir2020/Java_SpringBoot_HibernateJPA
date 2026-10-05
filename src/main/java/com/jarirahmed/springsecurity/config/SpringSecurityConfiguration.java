package com.jarirahmed.springsecurity.config;

import com.jarirahmed.springapi.config.SpringApiConfiguration;
import com.jarirahmed.springsecurity.controller.SecurityStatusController;
import com.jarirahmed.springsecurity.error.JsonSecurityErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Phase 10 composition root. The Phase 9 API remains the application layer;
 * this configuration adds the security filter chain around it.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Import(SpringApiConfiguration.class)
@ComponentScan(basePackageClasses = SecurityStatusController.class)
public class SpringSecurityConfiguration {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** In-memory users keep the first authentication example independent of another schema. */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails reader = User.withUsername("reader")
                .password(passwordEncoder.encode("reader-password"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin-password"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(reader, admin);
    }

    @Bean
    public JsonSecurityErrorHandler jsonSecurityErrorHandler() {
        return new JsonSecurityErrorHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JsonSecurityErrorHandler errorHandler) throws Exception {
        http
                // This is a stateless JSON API using an Authorization header.
                // Browser form sessions and CSRF protection are separate lessons.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler))
                .httpBasic(basic -> basic.authenticationEntryPoint(errorHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/security/public").permitAll()
                        .requestMatchers("/api/security/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/employees", "/api/employees/**")
                        .hasRole("ADMIN")
                        .anyRequest().denyAll());
        return http.build();
    }
}
