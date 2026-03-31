package com.arits.expense_trancker.dto;


import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTransactionHistoryAllDto {

private List<GetTransactionHistoryDto> owners;
private List<GetTransactionHistoryDto> subOwners;


}