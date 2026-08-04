package com.finance.ledger.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlySummaryResponse {

    private int year;
    private int month;
    private int totalIncome; //해당 월의 월수입
    private int totalExpense; //해달 월의 총지출
    private int balance; //총수입 - 총지출
}