package com.arits.expense_trancker.dto;

import java.math.BigDecimal;


public interface MobileAccountsDetailsDto {

     String getAccountHolder();
     Long getAccountId();
     String getAccountType();
     String getProviderName();
     String getPhoneNumber();
     BigDecimal getBalance();
     String getCurrency();
//     String getCreatedAt();

}
