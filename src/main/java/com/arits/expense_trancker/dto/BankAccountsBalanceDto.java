package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public interface BankAccountsBalanceDto {

     Long getId();
     String getBankName();
     String getAccountNumber();
     String getAccountType();
     BigDecimal getBalance();
     String getCurrency();

}
