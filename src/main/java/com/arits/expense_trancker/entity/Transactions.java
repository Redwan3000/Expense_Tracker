package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
@SQLDelete(sql="update transactions set is_deleted=true where transaction_id=?")
@SQLRestriction("is_deleted = false")
public class Transactions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;


    private BigDecimal amount;
    private String itemName;
    private String description;
    private LocalDate date;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "transaction_methods", nullable = false)
    private TransactionMethods transactionMethods;

    private String invoicePath;



}
