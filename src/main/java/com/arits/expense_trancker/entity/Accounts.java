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
@SQLDelete(sql = "UPDATE accounts SET is_deleted = true,deleted_at=NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Accounts {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private boolean isDeleted=false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "transaction_method_id", nullable = false)
    private TransactionMethod transactionMethod;



    @OneToOne(mappedBy = "accounts")
    private Balance balance;

    @OneToOne(mappedBy = "accounts")
    private BankAccountDetails bankAccountDetails;

    @OneToOne(mappedBy = "accounts")
    private CashWalletDetails cashWalletDetails;

    @OneToOne(mappedBy = "accounts")
    private MobileBankDetails mobileBankDetails;

    @OneToMany(mappedBy = "accounts")
    private Set<Transactions> transactions;

}
