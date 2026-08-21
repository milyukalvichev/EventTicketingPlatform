package com.ticketing.main_app.controller;

import com.ticketing.main_app.dto.UserRegisterDTO;
import com.ticketing.main_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("userRegisterDTO")) {
            model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        }
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("userRegisterDTO") UserRegisterDTO userRegisterDTO,
                                 BindingResult bindingResult,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        boolean success = userService.registerUser(userRegisterDTO);
        if (!success) {
            model.addAttribute("registrationError", "Username/Email is already taken or passwords do not match.");
            return "register";
        }

        return "redirect:/login?registered=true";
    }
}