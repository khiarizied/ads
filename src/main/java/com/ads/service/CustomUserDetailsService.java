package com.ads.service;


//src/main/java/com/example/realestate/service/CustomUserDetailsService.java

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ads.model.User;
import com.ads.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

 private final UserRepository userRepository;

 // Constructor injection to avoid circular dependency
 public CustomUserDetailsService(UserRepository userRepository) {
     this.userRepository = userRepository;
 }

 @Override
 public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
     User user = userRepository.findByUsername(username)
             .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

     return new org.springframework.security.core.userdetails.User(
             user.getUsername(),
             user.getPassword(),
             getAuthorities(user.getRole().name())
     );
 }

 private Collection<? extends GrantedAuthority> getAuthorities(String role) {
     return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
 }
}