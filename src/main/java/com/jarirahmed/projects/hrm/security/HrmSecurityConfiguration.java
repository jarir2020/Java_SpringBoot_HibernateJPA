package com.jarirahmed.projects.hrm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

/** Role fixtures make authorization visible without introducing a user-service migration yet. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class HrmSecurityConfiguration {
    @Bean
    public PasswordEncoder hrmPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService hrmUsers(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin-password"))
                .roles("ADMIN", "HR_MANAGER", "EMPLOYEE")
                .build();
        UserDetails hr = User.withUsername("hr")
                .password(passwordEncoder.encode("hr-password"))
                .roles("HR_MANAGER", "EMPLOYEE")
                .build();
        UserDetails employee = User.withUsername("employee")
                .password(passwordEncoder.encode("employee-password"))
                .roles("EMPLOYEE")
                .build();
        return new InMemoryUserDetailsManager(admin, hr, employee);
    }

    @Bean
    public SecurityFilterChain hrmSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(basic -> basic.realmName("Project 5 HRM"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/project5/**").permitAll()
                        .requestMatchers("/api/project5/**").authenticated()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().permitAll());
        return http.build();
    }
}
