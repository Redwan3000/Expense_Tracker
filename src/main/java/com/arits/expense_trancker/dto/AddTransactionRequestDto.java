package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddTransactionRequestDto {
    private String itemName;
    private BigDecimal amount;
    private Long paymentMethod;
    private Long transactionType;
    private String description;
    private Long accountId;


}
