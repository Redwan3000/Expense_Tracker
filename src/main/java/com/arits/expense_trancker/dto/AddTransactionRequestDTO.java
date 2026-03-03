package com.arits.expense_trancker.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddTransactionRequestDTO {

    private BigDecimal amount;
    private Long tMethod;
    private Long tType;
    private String description;
    private String itemName;
    private Long invoiceId;

}
