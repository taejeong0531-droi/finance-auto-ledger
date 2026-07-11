package com.finance.ledger.dto;

import lombok.Getter;
import com.finance.ledger.enums.Category;

@Getter
public class TransactionRequest {

    private Integer amount;

    private String description;

    private String type;

    private Category category;
}