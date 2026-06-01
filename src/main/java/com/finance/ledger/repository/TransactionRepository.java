package com.finance.ledger.repository;

import com.finance.ledger.entity.Transaction;
import com.finance.ledger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);
}