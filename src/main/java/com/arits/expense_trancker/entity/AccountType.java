package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@SQLDelete(sql = "update account_type set is_deleted=true , deleted_at = NOW() where id= ?")
@SQLRestriction("is_deleted=false")
public class AccountType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;



    @OneToMany(mappedBy = "accountType")
    private Set<Bank> bankAccounts;

    @OneToMany(mappedBy = "accountType")
    private Set<MobileBanking> mobileBankingAccounts;


}
