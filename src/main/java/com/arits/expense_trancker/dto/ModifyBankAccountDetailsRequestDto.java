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
public class ModifyBankAccountDetailsRequestDto {

private String accountNumber;
private String currencyId;
private String bankName;
private String bankBranch;
private String accountType;
private BigDecimal balance;


}
