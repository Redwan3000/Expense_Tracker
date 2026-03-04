package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class AddTransactionRequestDTO {
    private String itemName;
    private BigDecimal amount;
    private Long tMethod;
    private Long tType;
    private String description;


}
