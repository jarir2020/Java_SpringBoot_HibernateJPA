package com.jarirahmed.projects.storefront.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
 * Small Basic-auth boundary for the teaching storefront.
 *
 * <p>The browser uses the demo shopper credentials only on localhost. This is
 * not a production identity system.</p>
 */
@Configuration
@EnableWebSecurity
public class StorefrontSecurityConfiguration {
    @Bean
    public PasswordEncoder storefrontPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService storefrontUsers(PasswordEncoder passwordEncoder) {
        UserDetails shopper = User.withUsername("shopper")
                .password(passwordEncoder.encode("shopper-password"))
                .roles("SHOPPER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin-password"))
                .roles("SHOPPER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(shopper, admin);
    }

    @Bean
    public SecurityFilterChain storefrontSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.realmName("Project 4 Storefront"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/project4/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/project4/categories", "/api/project4/products").permitAll()
                        .requestMatchers("/api/project4/**").authenticated()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
