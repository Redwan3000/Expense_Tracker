package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AddTransactionRequestDto {
    private String itemName;
    private BigDecimal amount;
    private Long tMethod;
    private Long tType;
    private String description;
    private Long accountId;


}
