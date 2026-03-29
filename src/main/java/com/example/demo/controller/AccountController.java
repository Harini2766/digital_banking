package com.example.demo.controller;

import com.example.demo.entity.Account;
import com.example.demo.entity.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AccountService;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Create account (existing functionality)
    @PostMapping("/accounts/create")
    @ResponseBody
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    // =========================
    // Deposit Money
    // =========================
    @PostMapping("/deposit")
    public String deposit(@RequestParam Double amount, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        accountService.deposit(account.getAccountNumber(), amount);

        return "redirect:/dashboard";
    }

    // =========================
    // Withdraw Money
    // =========================
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Double amount, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        accountService.withdraw(account.getAccountNumber(), amount);

        return "redirect:/dashboard";
    }

    // =========================
    // Transfer Money
    // =========================
    @PostMapping("/transfer")
    public String transfer(@RequestParam String toAccountNumber,
                           @RequestParam Double amount, Principal principal) {

        // 🔹 DEBUG LOGS
        System.out.println("TRANSFER REQUEST:");
        System.out.println("To Account: " + toAccountNumber);
        System.out.println("Amount: " + amount);

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account fromAccount = accountRepository.findByUserId(user.getId());

        System.out.println("From Account: " + fromAccount.getAccountNumber());
        try {

            accountService.transfer(fromAccount.getAccountNumber(), toAccountNumber, amount);

            return "redirect:/transfer?success=Transaction Successful";

        } catch (Exception e) {

            return "redirect:/transfer?error=" + e.getMessage();
        }
    }
    @GetMapping("/getAccountName")
    @ResponseBody
    public String getAccountName(@RequestParam String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            return "Not Found";
        }

        return account.getUser().getName();
    }

    // =========================
    // PDF Download (placeholder)
    // =========================
    @GetMapping("/download-statement")
    @ResponseBody
    public String downloadStatement() {
        return "PDF feature coming soon"; // simple placeholder
    }
}