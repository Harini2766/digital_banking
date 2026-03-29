package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Deposit Money
    public Transaction deposit(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber);

        account.setBalance(account.getBalance() + amount);

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setToAccount(account);

        return transactionRepository.save(transaction);
    }

    // Withdraw Money
    public Transaction withdraw(String accountNumber, Double amount) {

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if(account.getBalance() < amount) {
            throw new RuntimeException("Insufficient Balance");
        }

        account.setBalance(account.getBalance() - amount);

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAW");
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setFromAccount(account);

        return transactionRepository.save(transaction);
    }

    // Transfer Money
    public Transaction transfer(String fromAccountNumber, String toAccountNumber, Double amount) {

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber);
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber);

        if(fromAccount.getBalance() < amount) {
        	throw new RuntimeException("Insufficient balance in account");
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

        return transactionRepository.save(transaction);
    }
}