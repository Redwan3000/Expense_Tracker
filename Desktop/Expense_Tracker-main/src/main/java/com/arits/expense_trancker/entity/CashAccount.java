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
public class CashAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal currentBalance;

    @ManyToOne
    private Currency currency;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


}
