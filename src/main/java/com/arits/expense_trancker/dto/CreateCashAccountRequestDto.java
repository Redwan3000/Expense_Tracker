package com.arits.expense_trancker.dto;

import com.arits.expense_trancker.entity.Currency;
import com.arits.expense_trancker.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCashAccountRequestDto {

    private String currency;
    private BigDecimal currentBalance;
}
