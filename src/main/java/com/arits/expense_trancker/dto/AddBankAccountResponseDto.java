package com.arits.expense_trancker.dto;

import com.arits.expense_trancker.entity.MobileBankingAccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddBankAccountResponseDto {

        private Long id;
        private String bankName;
        private String bankBranch;
        private String accountNumber;
        private String accountType;
        private String currencyName;
        private BigDecimal currentBalance;


        public AddBankAccountResponseDto(Long id, String providerName, MobileBankingAccountType accountType, String phoneNumber, BigDecimal currentBalance) {
        }
}
