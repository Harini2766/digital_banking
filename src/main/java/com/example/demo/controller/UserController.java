package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;  // ✅ change from RestController
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.dto.RegisterUserDTO;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import java.security.Principal;

@Controller  // ✅ important: use Controller, not RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // =========================
    // REGISTER USER
    // =========================
    @PostMapping("/register")
    @ResponseBody // keep JSON response for registration
    public User registerUser(@RequestBody RegisterUserDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setRole("USER");

        return userService.registerUser(user);
    }

    // =========================
    // CHANGE PASSWORD
    // =========================
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        try {
            String email = principal.getName();

            userService.changePassword(email, oldPassword, newPassword);

            redirectAttributes.addFlashAttribute("success", "Password updated successfully");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";  // ✅ will redirect to Thymeleaf profile page
    }
}