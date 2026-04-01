package com.arits.expense_trancker.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class AddBankAccountRequestDto {


    @NotBlank(message = "Bank name cannot be null")
    private String bankName;

    private String bankBranch;

    @NotBlank(message = "Account number cannot be null")
    private String accountNumber;

    @NotBlank(message = "Account type cannot be null")
    private String accountType;

    @NotBlank(message = "Currency cannot be null")
    private String currency;

    @NotNull(message = "balance can not be initialize with null value")
    @DecimalMin(value = "0.00")
    private BigDecimal currentBalance;

}
