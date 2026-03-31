package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeletedExpensesResponseDto {
    private Long tId;
    private String itemName;
    private BigDecimal amount;
    private String tMethod;
    private String tType;
    private String description;
    private String invoicePath;

}
