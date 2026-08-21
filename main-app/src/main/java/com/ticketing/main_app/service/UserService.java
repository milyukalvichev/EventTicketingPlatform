package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.UserRegisterDTO;
import com.ticketing.main_app.model.User;
import com.ticketing.main_app.model.UserRoleEnum;
import com.ticketing.main_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(UserRegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return false;
        }

        if (userRepository.findByUsername(dto.getUsername()).isPresent() ||
                userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return false;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(UserRoleEnum.USER);

        userRepository.save(user);
        return true;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}