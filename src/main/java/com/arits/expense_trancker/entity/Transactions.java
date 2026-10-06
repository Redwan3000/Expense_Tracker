package com.arits.expense_trancker.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Null;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "update transactions set is_deleted=true ,deleted_at=NOW() where id=?")
@SQLRestriction("is_deleted = false")
public class Transactions {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private String itemName;
    private String description;
    private LocalDate date;
    @Nullable
    private String invoicePath;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;


    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;


   @ManyToOne
    private User user;

   @ManyToOne
private PaymentMethod paymentMethod;



    @PrePersist
    public void prePersist(){
        this.createdAt= LocalDateTime.now();
        this.updatedAt= LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt= LocalDateTime.now();
    }
}
