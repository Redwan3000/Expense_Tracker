package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE currency SET is_deleted = true , deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private String name;


    @OneToMany(mappedBy = "currency")
    private Set<Bank> bankAccounts;

    @OneToMany(mappedBy = "currency")
    private Set<MobileBanking> mobileBankingAccounts;

    @OneToMany(mappedBy = "currency")
    private Set<CashWallet> cashWallets;

}
