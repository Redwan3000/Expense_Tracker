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
@Table(name = "transaction_methods")
@SQLDelete(sql = "update transaction_methods set is_deleted=true ,deleted_at=NOW()where tm_id=?")
@SQLRestriction("is_deleted=false")

public class TransactionMethods {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tm_id")
    private Long tmId;

    @Column(name = "method_name")
    private String methodName;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "transactionMethods")
    private Set<Transactions> transactions;

}
