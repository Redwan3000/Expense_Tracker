package com.arits.expense_trancker.entity;

import com.arits.expense_trancker.repository.PaymentMethodRepo;
import jakarta.persistence.*;
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
@SQLDelete(sql = "update bank set is_deleted=true , deleted_at = NOW() where id= ?")
@SQLRestriction("is_deleted=false")
public class Bank {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String accountNumber;

    private String bankName;
    private String bankBranch;
    private BigDecimal balance;

    @Builder.Default
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;


    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "account_type_id", nullable = false)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;


}
