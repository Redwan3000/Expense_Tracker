package com.arits.expense_trancker.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "update account_details set is_deleted = true,deleted_at=NOW() where id=?")
@SQLRestriction("is_deleted = false")
public class AccountDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Nullable
    @Builder.Default
    private String accountNumber=null;
    @Nullable
    @Builder.Default
    private String nomineeName=null;

    @Nullable
    @Builder.Default
    private String accountHolder= null;


    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "provider_id")
    private ProviderList provider;

    @OneToOne(mappedBy = "accountDetails",cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private Account account;

}
