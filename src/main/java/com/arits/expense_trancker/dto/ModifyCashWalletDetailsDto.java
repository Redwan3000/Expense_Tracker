package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ModifyCashWalletDetailsDto {

private String currency;
private BigDecimal balance;

}
