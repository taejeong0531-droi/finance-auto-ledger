package com.finance.ledger.dto;

import lombok.Getter;

@Getter
public class TransactionRequest {

    private Integer amount;

    private String description;

    private String type;
}