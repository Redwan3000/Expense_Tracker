package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transaction_type")
@SQLDelete(sql="update transaction_type set is_deleted=true where tt_id=?")
@SQLRestriction("is_deleted=false")
public class TransactionType {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tt_id")
    private Long ttId;

    @Builder.Default
    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Column(name = "type_name")
    private String typeName;

    @OneToMany(mappedBy = "transactionType")
    private List<Transactions> transactions = new ArrayList<>();

}
