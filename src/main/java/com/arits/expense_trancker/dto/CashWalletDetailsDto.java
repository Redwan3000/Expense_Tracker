package com.arits.expense_trancker.dto;

import java.math.BigDecimal;

public interface CashWalletDetailsDto {

     Long getWalletId();
     String getAccountHolder();
     BigDecimal getBalance();
     String getCurrency();
     String getPaymentMethod();

}
