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
public class ModifyAccountDetailsRequestDto {

    private Long currency;
    private Long accountType;
    private String accountNumber;
    private String accountHolder;
    private String nomineeName;
    private BigDecimal balance;
}
