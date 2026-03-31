package com.arits.expense_trancker.dto;


import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTransactionHistoryDto {

private Long transactionId;
private Long userId;
private String username;
private BigDecimal amount;
private String transactionMethod;
private String transactionType;
private long accountId;
private String itemName;
private String description;
private Boolean hasInvoice;

}