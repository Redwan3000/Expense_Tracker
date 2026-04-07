package com.arits.expense_trancker.dto;

import java.math.BigDecimal;


public interface MobileBankingAccountsBalanceDto {

     Long getId();
     String getProviderName();
     String getPhoneNumber();
     String getAccountType();
     BigDecimal getBalance();

}
