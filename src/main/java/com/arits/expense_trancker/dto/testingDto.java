package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class testingDto {


    private Long userId;

    private String userName;

    private String bankName;

    private String accountType;
}
