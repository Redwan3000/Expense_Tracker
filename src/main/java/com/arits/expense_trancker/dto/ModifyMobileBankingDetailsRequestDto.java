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
public class ModifyMobileBankingDetailsRequestDto {


    private String providerName;

    private String accountType;

    private String phoneNumber;

    private BigDecimal currentBalance;


}
