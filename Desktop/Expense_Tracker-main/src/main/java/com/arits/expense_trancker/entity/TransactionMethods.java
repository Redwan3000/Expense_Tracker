package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionMethods {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tmId;

    @Column(name = "method_name")
    private String methodName;

    @OneToMany(mappedBy = "transactionMethods")
    private Set<Transactions> transactions;

}
