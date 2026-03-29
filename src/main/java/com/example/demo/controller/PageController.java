package com.example.demo.controller;
import org.springframework.web.bind.annotation.RequestParam;
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

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;

// ✅ For chart JSON conversion
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Home → open login page
    @GetMapping("/")
    public String home() {
        return "login";
    }

    // 🔥 REQUIRED for Spring Security login
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 🔥 REQUIRED to open register page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // After login success
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        // 🔥 Fetch top 5 latest transaction history
        List<Transaction> transactions =
            transactionRepository.findTop5ByFromAccount_IdOrToAccount_IdOrderByTransactionDateDesc(
                account.getId(), account.getId()
            );

        model.addAttribute("balance", account.getBalance());
        model.addAttribute("accountNumber", account.getAccountNumber());
        model.addAttribute("transactions", transactions);

        return "dashboard";
    }

    @GetMapping("/deposit")
    public String depositPage(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        model.addAttribute("account", account);

        return "deposit";
    }

    @GetMapping("/withdraw")
    public String withdrawPage(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        model.addAttribute("account", account);

        return "withdraw";
    }

    @GetMapping("/transfer")
    public String transferPage(Model model,
                               Principal principal,
                               @RequestParam(required = false) String error,
                               @RequestParam(required = false) String success) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        model.addAttribute("account", account);
        model.addAttribute("error", error);
        model.addAttribute("success", success);

        return "transfer";
    }

    // 🔥 FULL TRANSACTION PAGE
    @GetMapping("/transactions")
    public String transactionsPage(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        List<Transaction> transactions =
            transactionRepository.findByFromAccount_IdOrToAccount_IdOrderByTransactionDateDesc(
                account.getId(), account.getId()
            );

        model.addAttribute("transactions", transactions);

        return "transactions";
    }

    // 🔥 UPDATED PROFILE PAGE
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);
        Account account = accountRepository.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("account", account);

        return "profile";
    }

    // 🔥 NEW ACCOUNTS PAGE TO FIX WHITE LABEL ERROR & SHOW DETAILS
    @GetMapping("/accounts")
public String accountsPage(Model model, Principal principal) {

    String email = principal.getName();
    User user = userRepository.findByEmail(email);
    Account account = accountRepository.findByUserId(user.getId());

    List<Transaction> transactions =
        transactionRepository.findByFromAccount_IdOrToAccount_IdOrderByTransactionDateDesc(
            account.getId(), account.getId()
        );

    int totalDeposit = 0;
    int totalWithdraw = 0;

    for (Transaction t : transactions) {

        if ("DEPOSIT".equals(t.getTransactionType())) {
            totalDeposit++;   // ✅ count
        }
        else if ("WITHDRAW".equals(t.getTransactionType())) {
            totalWithdraw++;  // ✅ count
        }
    }
    // 🔥 GRAPH LOGIC (PER DAY COUNT - CURRENT MONTH)
    int[] dailyCount = new int[31]; // index 1–30

    int currentMonth = java.time.LocalDate.now().getMonthValue();

    for (Transaction t : transactions) {

        if (t.getTransactionDate().getMonthValue() == currentMonth) {

            int day = t.getTransactionDate().getDayOfMonth();

            if (day <= 30) {
                dailyCount[day]++;
            }
        }
    }

    List<Integer> chartDates = new ArrayList<>();
    List<Integer> chartCounts = new ArrayList<>();

    for (int i = 1; i <= 30; i++) {
        chartDates.add(i);          // 1,2,3...
        chartCounts.add(dailyCount[i]); // count per day
    }

    model.addAttribute("account", account);
    model.addAttribute("totalDeposit", totalDeposit);
    model.addAttribute("totalWithdraw", totalWithdraw);
    model.addAttribute("transactionCount", transactions.size());
    model.addAttribute("lastLogin", "Today");

    model.addAttribute("chartDates", chartDates);
    model.addAttribute("chartCounts", chartCounts);

    return "accounts";
}
}