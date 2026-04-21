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
public class BalanceTransferResponseDto {

    private Long fromAccountId;
    private String fromAccountNumber;
    private BigDecimal transferredAmount;
    private BigDecimal remainingBalance;
    private LocalDateTime transferredAt;

}
