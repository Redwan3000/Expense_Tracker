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
public class OverallBalanceDto {

private Long id;
private BigDecimal totalBalance;
private BigDecimal bankBalance;
private  BigDecimal mobileBankingBalance;
private BigDecimal cashBalance;
}

