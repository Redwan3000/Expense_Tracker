package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCashAccountResponseDto {

    private Long id;
    private String currencyName;
    private BigDecimal currentBalance;
    private String paymentMethod;

}
