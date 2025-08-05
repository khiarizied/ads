package com.ads.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ads.dto.UserDto;
import com.ads.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<UserDto> users = userService.findAllUsers();
        long userCount = users.size();
        long adminCount = users.stream().filter(u -> u.getRole().name().equals("ADMIN")).count();
        
        model.addAttribute("userCount", userCount);
        model.addAttribute("adminCount", adminCount);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
    	List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
}
    