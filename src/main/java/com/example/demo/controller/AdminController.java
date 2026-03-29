package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // ✅ Dashboard (show recent 10 non-admin users)
    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("accountCount", accountRepository.count());
        model.addAttribute("transactionCount", transactionRepository.count());

        // 🔹 Only last 10 non-admin users
        List<User> recentUsers = userRepository.findAll()
            .stream()
            .filter(u -> u.getRole() != null && !u.getRole().trim().equalsIgnoreCase("admin"))
            .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
            .limit(10)
            .toList();

        model.addAttribute("users", recentUsers);

        return "admin-dashboard";
    }

    // ✅ Users Page (no admins, with search)
    @GetMapping("/admin/users")
    public String users(Model model,
                        @RequestParam(required = false) String search) {

        List<User> users = userRepository.findAll()
            .stream()
            .filter(u -> u.getRole() != null && !u.getRole().trim().equalsIgnoreCase("admin"))
            .filter(u -> search == null || u.getName().toLowerCase().contains(search.toLowerCase()))
            .toList();

        model.addAttribute("users", users);
        model.addAttribute("search", search);

        return "admin-users";
    }

    // ✅ Accounts Page (no admins, with search)
    @GetMapping("/admin/accounts")
    public String accounts(Model model,
                           @RequestParam(required = false) String search) {

        List<Account> accounts = accountRepository.findAll()
            .stream()
            .filter(acc -> acc.getUser() != null &&
                           acc.getUser().getRole() != null &&
                           !acc.getUser().getRole().trim().equalsIgnoreCase("admin"))
            .filter(acc -> search == null || acc.getAccountNumber().toLowerCase().contains(search.toLowerCase()))
            .toList();

        model.addAttribute("accounts", accounts);
        model.addAttribute("search", search);

        return "admin-accounts";
    }

    // ✅ Transactions Page (FILTER)
    @GetMapping("/admin/transactions")
    public String transactions(Model model,
                               @RequestParam(required = false) String type) {

        List<Transaction> transactions;

        if(type != null && !type.isEmpty()){
            transactions = transactionRepository.findByTransactionType(type);
        } else {
            transactions = transactionRepository.findAll();
        }

        model.addAttribute("transactions", transactions);

        return "admin-transactions";
    }

    // 🚫 BLOCK / UNBLOCK USER
    @GetMapping("/admin/user/toggle/{id}")
    public String toggleUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);

        if(user != null){
            if("ACTIVE".equals(user.getStatus())){
                user.setStatus("BLOCKED");
            } else {
                user.setStatus("ACTIVE");
            }
            userRepository.save(user);
        }

        return "redirect:/admin/users";
    }

    // ❌ DELETE USER
    @GetMapping("/admin/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // 🏦 ACTIVATE / DEACTIVATE ACCOUNT
    @GetMapping("/admin/account/toggle/{id}")
    public String toggleAccount(@PathVariable Long id) {
        Account acc = accountRepository.findById(id).orElse(null);

        if(acc != null){
            if("ACTIVE".equals(acc.getStatus())){
                acc.setStatus("BLOCKED");
            } else {
                acc.setStatus("ACTIVE");
            }
            accountRepository.save(acc);
        }

        return "redirect:/admin/accounts";
    }

    // 🔒 LOGOUT
    @GetMapping("/admin/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/login"; // replace with your login page URL
    }
}