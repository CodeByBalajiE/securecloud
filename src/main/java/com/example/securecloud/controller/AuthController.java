package com.example.securecloud.controller;

import com.example.securecloud.model.User;
import com.example.securecloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        boolean ok = userService.login(user.getEmail(), user.getPassword());
        return ok
                ? ResponseEntity.ok("Login success")
                : ResponseEntity.status(401).body("Invalid credentials");
    }

}
