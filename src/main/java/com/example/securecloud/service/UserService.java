package com.example.securecloud.service;

import com.example.securecloud.model.Role;
import com.example.securecloud.model.User;
import com.example.securecloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String register(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepo.save(user);
        return "Registration successful";
    }

    public boolean login(String email, String password) {
        Optional<User> opt = userRepo.findByEmail(email);
        return opt.isPresent() && passwordEncoder.matches(password, opt.get().getPassword());
    }

    public Optional<User> getUser(Long id) {
        return userRepo.findById(id);
    }
}