package com.arits.expense_trancker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "mobile_banking",uniqueConstraints = {@UniqueConstraint(columnNames = {"providerName", "phoneNumber"})})
@SQLDelete(sql = "UPDATE mobile_banking SET is_deleted = true , deleted_at = NOW() WHERE id=?")
@SQLRestriction("is_deleted = false")
public class MobileBanking {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerName;

    @Enumerated(EnumType.STRING)
    private MobileBankingAccountType accountType;


    private String phoneNumber;

    private BigDecimal currentBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;



}
