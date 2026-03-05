package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTransactionResponseDTO {
    private String itemName;
    private BigDecimal amount;
    private String tMethod;
    private String tType;
    private String description;
    private String invoicePath;

}
