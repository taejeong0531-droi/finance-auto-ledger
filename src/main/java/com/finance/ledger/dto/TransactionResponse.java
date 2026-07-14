package com.finance.ledger.dto;

import com.finance.ledger.entity.Transaction;
import com.finance.ledger.enums.Category;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TransactionResponse {

    private Long id;
    private Integer amount;
    private String description;
    private String type;
    private Category category;
    private LocalDate date;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.type = transaction.getType();
        this.category = transaction.getCategory();
        this.date = transaction.getDate();
    }
}