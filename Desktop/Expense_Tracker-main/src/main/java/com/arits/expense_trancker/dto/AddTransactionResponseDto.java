package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddTransactionResponseDto {
    private long trxId;
    private String itemName;
    private BigDecimal amount;
    private String tMethod;
    private String tType;
    private long accountId;
    private String description;
    private String invoicePath;

}
