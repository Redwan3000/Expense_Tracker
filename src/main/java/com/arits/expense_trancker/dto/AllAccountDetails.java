package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AllAccountDetails {

    private BigDecimal totalBalance;
    private List<AccountDetails> bankAccounts;
    private List<AccountDetails>mobileBankingAccounts;
    private List<AccountDetails>cashWallets;


}
