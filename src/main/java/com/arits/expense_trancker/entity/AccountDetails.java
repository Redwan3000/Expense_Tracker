package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(mappedBy = "accountDetails")
    private CashWalletDetails cashWalletDetails;


    @OneToOne(mappedBy = "")
    private




}
