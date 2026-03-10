package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;
    private String bankBranch;

    @Column(unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private BankAccountType accountType;

    @ManyToOne(fetch = FetchType.EAGER) // Usually Eager because you always need the currency name
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    private BigDecimal currentBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;


}
