package com.ads.controller;

import com.ads.model.User;
import com.ads.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping({"/", "/home"})
    public String home() {
        return "home/index";
    }

    @GetMapping("/photos/users/{username}")
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable String username) {
        Optional<User> user = Optional.ofNullable(userService.findByUsername(username));
        if (user.isPresent() && user.get().hasPhoto()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(user.get().getPhotoType()));
            return new ResponseEntity<>(user.get().getPhoto(), headers, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
