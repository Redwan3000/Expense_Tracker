package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@SQLDelete(sql = "update cash_wallet_details set is_deleted=true , deleted_at = NOW() where id= ?")
@SQLRestriction("is_deleted=false")
public class CashWalletDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String walletName;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;

    @OneToOne
    @JoinColumn(name = "account_details")
    private AccountDetails accountDetails;

}
