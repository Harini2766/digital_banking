package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Account;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(User user){

        // Set default role
        user.setRole("ROLE_USER");

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // 🔥 Create account automatically
        Account account = new Account();
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        account.setAccountType("SAVINGS");
        account.setBalance(0.0);
        account.setStatus("ACTIVE");
        account.setUser(savedUser);  // Link account to user

        accountRepository.save(account);

        return "redirect:/login";
    }
}