package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllAccountBalanceDto {

    private BigDecimal totalBalance;
    private List<BankAccountsBalanceDto> bankAccounts;
    private List<MobileBankingAccountsBalanceDto>mobileBankingAccounts;
    private List<CashAccountsBalanceDto>cashAccounts;


}
