package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAccountRequestDto {

    private Long currency;
    private Long accountType;
    private Long paymentMethod;
    private String accountNumber;
    private String nomineeName;
    private Long provider;
    private BigDecimal balance;
    private String accountHolder;

}
