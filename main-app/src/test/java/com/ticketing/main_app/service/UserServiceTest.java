package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.UserRegisterDTO;
import com.ticketing.main_app.model.User;
import com.ticketing.main_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_PasswordsMismatch_ReturnsFalse() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("user1");
        dto.setEmail("user1@mail.com");
        dto.setPassword("pass123");
        dto.setConfirmPassword("pass456");

        boolean success = userService.registerUser(dto);
        assertFalse(success);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_ValidData_ReturnsTrue() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("user1");
        dto.setEmail("user1@mail.com");
        dto.setPassword("pass123");
        dto.setConfirmPassword("pass123");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user1@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPassword");

        boolean success = userService.registerUser(dto);
        assertTrue(success);
        verify(userRepository, times(1)).save(any(User.class));
    }
}