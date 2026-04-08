package com.arits.expense_trancker.dto;


import java.math.BigDecimal;

public interface BankAccountsBalanceDto {

    Long getId();

    String getBankName();

    String getAccountNumber();

    String getAccountType();

    BigDecimal getBalance();

    String getCurrency();

    String getAccountHolder();

}
