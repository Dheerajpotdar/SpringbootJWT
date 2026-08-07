package com.example.authservice.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user/profile")
    public String userProfile() {
        return "Welcome User!";
    }

}