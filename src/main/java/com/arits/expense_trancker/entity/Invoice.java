package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("is_deleted=false")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "file_name")
    private String fileName;


    @Column(name = "file_path")
    private String filePath;


    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;


    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transactions transactions;




}
