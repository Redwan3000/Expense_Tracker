package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "file_name")
    private String fileName;


    @Column(name = "file_path")
    private String filePath;



    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transactions transactions;




}
