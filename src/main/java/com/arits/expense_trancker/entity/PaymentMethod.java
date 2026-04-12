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
@Table(name = "payment_method")
@SQLDelete(sql = "update payment_method set is_deleted=true ,deleted_at=NOW()where id=?")
@SQLRestriction("is_deleted=false")

public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;





    @OneToMany(mappedBy = "paymentMethod")
    private Set<Transactions> transactions;

    @OneToMany(mappedBy = "paymentMethod")
    private Set<Banks> banks;

    @OneToMany(mappedBy = "paymentMethod")
    private Set<MobileBanks> mobileBanks;

    @OneToMany(mappedBy = "paymentMethod")
    private Set<CashWalletDetails> cashWalletDetails;



}
