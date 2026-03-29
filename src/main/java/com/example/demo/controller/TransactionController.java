package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.Transaction;
import com.example.demo.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public Transaction deposit(@RequestParam String accountNumber,
                               @RequestParam Double amount) {

        return transactionService.deposit(accountNumber, amount);
    }

    @PostMapping("/withdraw")
    public Transaction withdraw(@RequestParam String accountNumber,
                                @RequestParam Double amount) {

        return transactionService.withdraw(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public Transaction transfer(@RequestParam String fromAccount,
                                @RequestParam String toAccount,
                                @RequestParam Double amount) {

        return transactionService.transfer(fromAccount, toAccount, amount);
    }
}