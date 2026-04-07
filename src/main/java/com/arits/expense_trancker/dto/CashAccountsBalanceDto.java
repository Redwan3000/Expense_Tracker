package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public interface CashAccountsBalanceDto {

     Long getId();
     BigDecimal getBalance();
     String getCurrency();
}
