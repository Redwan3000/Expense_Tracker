package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.AddBankAccountRequestDto;
import com.arits.expense_trancker.dto.AddBankAccountResponseDto;
import com.arits.expense_trancker.entity.BankAccount;
import com.arits.expense_trancker.entity.BankAccountType;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.BankAccountRepo;
import com.arits.expense_trancker.repository.CurrencyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountService {


    private final CurrencyRepo currencyRepo;
    private final BankAccountRepo bankAccountRepo;

    public AddBankAccountResponseDto addAccount(User user, AddBankAccountRequestDto addBankAccountRequestDTO) {


        BankAccount newAccount = BankAccount.builder()

                .bankName(addBankAccountRequestDTO.getBankName())
                .bankBranch(addBankAccountRequestDTO.getBankBranch())
                .accountNumber(addBankAccountRequestDTO.getAccountNumber())
                .accountType(BankAccountType.valueOf(addBankAccountRequestDTO.getAccountType()))
                .currency(currencyRepo.findByCurrencyName(addBankAccountRequestDTO.getCurrency()).orElseThrow(() -> new RuntimeException("currency could not found")))
                .currentBalance(addBankAccountRequestDTO.getCurrentBalance())
                .user(user)
                .build();
        bankAccountRepo.save(newAccount);

        return AddBankAccountResponseDto.builder()
                .id(newAccount.getId())
                .bankName(newAccount.getBankName())
                .bankBranch(newAccount.getBankBranch())
                .accountNumber(newAccount.getAccountNumber())
                .accountType(String.valueOf(newAccount.getAccountType()))
                .currencyName(newAccount.getCurrency().getCurrencyName())
                .currentBalance(newAccount.getCurrentBalance())
                .build();

    }
}
