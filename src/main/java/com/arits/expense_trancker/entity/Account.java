package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update account set is_deleted = true,deleted_at=NOW() where id=?")
@SQLRestriction("is_deleted = false")
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transaction_method_id", nullable = false)
    private PaymentMethod paymentMethod;


    @OneToOne(mappedBy = "account",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Balance balance;

    @OneToMany(mappedBy = "account",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Transactions> transactions;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "account_details")
    private AccountDetails accountDetails;

}
