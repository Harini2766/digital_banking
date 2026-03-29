package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Transaction;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 🔥 Fetch all transactions where the account is either sender or receiver
    List<Transaction> findByFromAccount_IdOrToAccount_Id(Long fromId, Long toId);

    // 🔥 Fetch top 5 latest transactions for dashboard
    List<Transaction> findTop5ByFromAccount_IdOrToAccount_IdOrderByTransactionDateDesc(
        Long fromId, Long toId
    );

    // 🔥 Fetch all transactions sorted by newest first (for transactions page)
    List<Transaction> findByFromAccount_IdOrToAccount_IdOrderByTransactionDateDesc(
        Long fromId, Long toId
    );
    List<Transaction> findByTransactionType(String type);
}