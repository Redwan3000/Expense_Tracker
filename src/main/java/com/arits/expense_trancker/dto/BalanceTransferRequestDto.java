package com.arits.expense_trancker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceTransferRequestDto {


    @NotNull(message = "fromMethodId is required")
    private Long fromMethodId;

    @NotNull(message = "toMethodId is required")
    private Long toMethodId;

    @NotNull(message = "receiverUserId must not be null")
    private Long receiverUserId;

    @NotNull(message = "fromAccountId is required")
    private Long fromAccountId;

    @NotNull(message = "toAccountId is required")
    private Long toAccountId;

    @NotNull(message = "transferAmount is required")
    @DecimalMin(value = "0.01", message = "transfer amount must be greater than 0")
    private BigDecimal transferAmount;

}
