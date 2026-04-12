package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MobileBankAccountDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "mfs_provider_name")
    private MobileBanks mobileBank;


}
