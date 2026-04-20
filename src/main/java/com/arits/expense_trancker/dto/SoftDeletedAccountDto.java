package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SoftDeletedAccountDto {

    private Long accountId;
    private String accountNumber;
    private String paymentMethod;
    private String providerName;
    private LocalDateTime deletedAt;


}
