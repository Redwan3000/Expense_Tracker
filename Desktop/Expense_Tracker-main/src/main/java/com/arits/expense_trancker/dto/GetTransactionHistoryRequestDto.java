package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTransactionHistoryRequestDto {
    private String itemName;
    private String username;
    private LocalDate singleDate;
    private LocalDate fromDate;
    private LocalDate toDate;
}
