package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transactions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;


    private double amount;
    private String itemName;
    private String description;
    private LocalDate date;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "transaction_methods", nullable = false)
    private TransactionMethods transactionMethods;

    @OneToOne(mappedBy = "transactions")
    private Invoice invoice;

}
