package com.ads.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ads.dto.UserDto;
import com.ads.model.Role;
import com.ads.model.User;
import com.ads.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(UserDto userDto) throws IOException {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
 user.setRole(userDto.getRole() != null ? userDto.getRole() : Role.USER);
        
        // Handle photo upload
        if (userDto.getPhotoFile() != null && !userDto.getPhotoFile().isEmpty()) {
            MultipartFile photoFile = userDto.getPhotoFile();
            
            // Validate file size (max 2MB)
            if (photoFile.getSize() > 2 * 1024 * 1024) {
                throw new IOException("File size exceeds 2MB limit");
            }
            
            // Validate file type
            String contentType = photoFile.getContentType();
            if (contentType == null || 
                (!contentType.startsWith("image/jpeg") && 
                 !contentType.startsWith("image/png") && 
                 !contentType.startsWith("image/gif"))) {
                throw new IOException("Invalid file type. Only JPG, PNG, and GIF are allowed");
            }
            
            user.setPhoto(photoFile.getBytes());
            user.setPhotoType(contentType);
        }
        
        return userRepository.save(user);
    }
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole(),
                        user.getPhoto(),
                        user.getPhotoType()))
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}