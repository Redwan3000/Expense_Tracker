package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBalanceDto {

    private Long accountId;
    private String accountNumber;
    private String paymentMethod;
    private String currency;
    private BigDecimal balance;
    private LocalDateTime lastUpdatedAt;

}
