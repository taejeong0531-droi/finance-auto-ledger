package com.finance.ledger.repository;

import com.finance.ledger.entity.Transaction;
import com.finance.ledger.entity.User;
import com.finance.ledger.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    boolean existsByUserAndDateAndAmountAndDescriptionAndTypeAndCategory(
            User user,
            LocalDate date,
            Integer amount,
            String description,
            String type,
            Category category
    );
}