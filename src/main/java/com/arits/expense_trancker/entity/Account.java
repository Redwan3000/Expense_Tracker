package com.arits.expense_trancker.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    private BigDecimal currentBalance;
    private BigDecimal curretnDebt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
