package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviveTransactionResponseDto {

    private long trxId;
    private long userId;
    private String transactionMethod;
    private String transactionType;
    private String description;

}
