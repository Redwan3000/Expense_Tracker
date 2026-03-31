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
public class MobileBankingAccountsBalanceDto {

    private Long id;
    private String providerName;
    private String phoneNumber;
    private String accountType;
    private BigDecimal balance;

}
