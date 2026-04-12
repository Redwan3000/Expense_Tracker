package com.arits.expense_trancker.entity;

import jakarta.persistence.*;

@Entity

public class BankAccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankBranch;
    private String accountNumber;
    private String nomineeName;

    @ManyToOne
    @JoinColumn(name = "bank_name")
    private Banks bank;

}
