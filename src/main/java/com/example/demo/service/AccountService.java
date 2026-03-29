package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;

import java.time.LocalDateTime;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // 🔐 MAX BALANCE LIMIT
    private static final double MAX_BALANCE = 10000000; // 1 crore limit

    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    // =========================
    // DEPOSIT
    // =========================
    public void deposit(String accountNumber, Double amount) {

        if(amount <= 0){
            throw new RuntimeException("Invalid amount");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);

        // 🔧 FIX NULL BALANCE
        if(account.getBalance() == null){
            account.setBalance(0.0);
        }

        // 🔐 PREVENT MAX BALANCE EXCEEDED
        if(account.getBalance() + amount > MAX_BALANCE){
            throw new RuntimeException("Balance limit exceeded");
        }

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setToAccount(account);

        transactionRepository.save(transaction);
    }

    // =========================
    // WITHDRAW
    // =========================
    public void withdraw(String accountNumber, Double amount) {

        if(amount <= 0){
            throw new RuntimeException("Invalid amount");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);

        // 🔧 FIX NULL BALANCE
        if(account.getBalance() == null){
            account.setBalance(0.0);
        }

        // 🔧 PREVENT NEGATIVE BALANCE
        if(account.getBalance() < amount){
            throw new RuntimeException("Insufficient Balance");
        }

        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAW");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setFromAccount(account);

        transactionRepository.save(transaction);
    }

    // =========================
    // TRANSFER
    // =========================
    public void transfer(String fromAccountNumber, String toAccountNumber, Double amount) {

        if(amount <= 0){
            throw new RuntimeException("Invalid amount");
        }

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        if(fromAccount == null || toAccount == null){
            throw new RuntimeException("Account not found");
        }

        // 🔧 FIX NULL BALANCE
        if(fromAccount.getBalance() == null){
            fromAccount.setBalance(0.0);
        }
        if(toAccount.getBalance() == null){
            toAccount.setBalance(0.0);
        }

        // 🔧 PREVENT NEGATIVE BALANCE
        if(fromAccount.getBalance() < amount){
            throw new RuntimeException("Insufficient balance");
        }

        // 🔐 PREVENT RECEIVER MAX BALANCE EXCEEDED
        if(toAccount.getBalance() + amount > MAX_BALANCE){
            throw new RuntimeException("Receiver balance limit exceeded");
        }

        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("TRANSFER");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);

        transactionRepository.save(transaction);
    }

    // =========================
    // GET BALANCE
    // =========================
    public Double getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        return account.getBalance();
    }
}