package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "mobile_banking",uniqueConstraints = {@UniqueConstraint(columnNames = {"providerName","phonenumber"})})
public class MobileBanking {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerName;

    @Enumerated(EnumType.STRING)
    private MobileBankingAccountType accountType;


    private String phoneNumber;

    private BigDecimal currentBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;



}
