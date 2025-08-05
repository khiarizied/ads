package com.ads.config;


//src/main/java/com/example/realestate/config/SecurityConfig.java



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.ads.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

 // Remove @Autowired and create beans directly
 @Bean
 public PasswordEncoder passwordEncoder() {
     return new BCryptPasswordEncoder();
 }

 @Bean
 public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService) {
     DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
     authProvider.setUserDetailsService(userDetailsService);
     authProvider.setPasswordEncoder(passwordEncoder());
     return authProvider;
 }

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     http
         .authorizeHttpRequests(authz -> authz
             .requestMatchers("/", "/home", "/auth/**", "/css/**", "/js/**", "/photos/**").permitAll()
             .requestMatchers("/admin/**").hasRole("ADMIN")
             .anyRequest().authenticated()
         )
         .formLogin(form -> form
             .loginPage("/auth/login")
             .defaultSuccessUrl("/admin/dashboard", true)
             .permitAll()
         )
         .logout(logout -> logout
             .logoutUrl("/logout")
             .logoutSuccessUrl("/")
             .permitAll()
         )
         .exceptionHandling(ex -> ex
             .accessDeniedPage("/access-denied")
         );
     return http.build();
 }
}