package com.arits.expense_trancker.dto;

import java.math.BigDecimal;


public interface AccountBalanceResponseDto {


     Long getId();
     String getPaymentMethod();
     BigDecimal getBalance();
     String getCurrency();


}
