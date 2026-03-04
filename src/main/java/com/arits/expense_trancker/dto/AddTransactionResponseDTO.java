package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class AddTransactionResponseDTO {
    private String itemName;
    private BigDecimal amount;
    private String tMethod;
    private String tType;
    private String description;
    private String invoicePath;

}
