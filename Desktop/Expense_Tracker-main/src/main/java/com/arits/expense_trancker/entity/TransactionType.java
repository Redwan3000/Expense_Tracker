package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ttId;


    @Column(name = "type_name")
    private String typeName;

    @OneToMany(mappedBy = "transactionType")
    private List<Transactions> transactions = new ArrayList<>();

}
