package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.CreateCashAccountRequestDto;
import com.arits.expense_trancker.dto.CreateCashAccountResponseDto;
import com.arits.expense_trancker.entity.CashAccount;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.CashAccountRepo;
import com.arits.expense_trancker.repository.CurrencyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashAccountService {


    private final CurrencyRepo currencyRepo;
    private final CashAccountRepo cashAccountRepo;

    public CreateCashAccountResponseDto addAccount(User user, CreateCashAccountRequestDto dto) {

        CashAccount newAccount= CashAccount.builder()
                .currentBalance(dto.getCurrentBalance())
                .currency(currencyRepo.findByCurrencyName(dto.getCurrency()).orElseThrow(()->new RuntimeException("Currency does not exist")))
                .user(user)
                .build();
        cashAccountRepo.save(newAccount);

        return CreateCashAccountResponseDto.builder()
                .id(newAccount.getId())
                .currencyName(newAccount.getCurrency().getCurrencyName())
                .currentBalance(newAccount.getCurrentBalance())
                .build();

    }
}
