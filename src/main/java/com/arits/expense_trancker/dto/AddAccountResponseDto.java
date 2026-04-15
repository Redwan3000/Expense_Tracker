package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAccountResponseDto {

    private Long accountId;
    private String currency;
    private String accountType;
    private String paymentMethod;
    private String accountNumber;
    private String nomineeName;
    private String providerName;
    private BigDecimal currentBalance;
    private String AccountHolder;
    private LocalDateTime createdAt;

}
