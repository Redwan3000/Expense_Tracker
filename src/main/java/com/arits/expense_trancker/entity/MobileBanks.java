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
@SQLDelete(sql = "UPDATE mobile_banks SET is_deleted = true , deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class MobileBanks {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerName;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "mobileBank")
    private Set<MobileBankAccountDetails> mfs;



}
