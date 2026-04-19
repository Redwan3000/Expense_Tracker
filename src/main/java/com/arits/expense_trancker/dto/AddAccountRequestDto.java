package com.arits.expense_trancker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "currency is required")
    private Long currency;

    @NotNull(message = "accountType is required")
    private Long accountType;

    @NotNull(message = "paymentMethod is required")
    private Long paymentMethod;

    @NotBlank(message = "accountNumber is required")
    private String accountNumber;

    private String nomineeName;

    @NotNull(message = "providerId is required")
    private Long provider;

    @NotNull(message = "currentBalance is required")
    private BigDecimal balance;
    @NotBlank(message = "accountHolder is required")
    private String accountHolder;

}
