package com.arits.expense_trancker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface AccountDetails {


      Long getAccountId();
      String getCurrency();
      String getAccountType();
      String getPaymentMethod();
      String getAccountNumber();
      String getNomineeName();
      String getProviderName();
      BigDecimal getCurrentBalance();
      String getAccountHolder();
      LocalDateTime getCreatedAt();
}
