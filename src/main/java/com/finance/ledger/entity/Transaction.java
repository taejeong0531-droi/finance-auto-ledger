package com.finance.ledger.entity;


import com.finance.ledger.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

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

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction(Integer amount,
                       String description,
                       String type,
                       Category category,
                       User user) {

        this(amount, description, type, category, user, LocalDate.now());
    }

    public Transaction(Integer amount,
                       String description,
                       String type,
                       Category category,
                       User user,
                       LocalDate date) {

        this.amount = amount;
        this.description = description;
        this.type = type;
        this.category = category;
        this.user = user;
        this.date = date;
    }

    public void update(Integer amount, String description, String type,Category category) {
        this.amount = amount;
        this.description = description;
        this.type = type;
        this.category = category;
    }




}