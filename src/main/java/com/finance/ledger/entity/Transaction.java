package com.finance.ledger.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer amount;

    private String description;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction(Integer amount, String description, String type, User user) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.user = user;
    }

    public void update(Integer amount, String description, String type) {
        this.amount = amount;
        this.description = description;
        this.type = type;
    }
}