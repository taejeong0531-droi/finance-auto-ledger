package com.finance.ledger.dto;

import com.finance.ledger.entity.Transaction;
import lombok.Getter;

@Getter
public class TransactionResponse {

    private Long id;
    private Integer amount;
    private String description;
    private String type;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.type = transaction.getType();
    }
}