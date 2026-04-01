package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountsBalanceDto {

    private Long id;
    private String bankName;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String currency;

}
