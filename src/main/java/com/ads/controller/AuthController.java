package com.ads.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ads.dto.UserDto;
import com.ads.service.UserService;

import java.io.IOException;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                               Model model) {
        // Add photoFile to model for re-display on validation errors
        userDto.setPhotoFile(photoFile);
        
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.existsByUsername(userDto.getUsername())) {
            model.addAttribute("usernameExists", true);
            return "auth/register";
        }

        if (userService.existsByEmail(userDto.getEmail())) {
            model.addAttribute("emailExists", true);
            return "auth/register";
        }

        try {
            userDto.setPhotoFile(photoFile);
            userService.save(userDto);
            return "redirect:/auth/login?registered";
        } catch (IOException e) {
        	   model.addAttribute("uploadError", e.getMessage());
               return "auth/register";
           }
       }
   }
      
