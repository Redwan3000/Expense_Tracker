package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.BankAccountRepo;
import com.arits.expense_trancker.repository.CashAccountRepo;
import com.arits.expense_trancker.repository.CurrencyRepo;
import com.arits.expense_trancker.repository.MobileBankingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class AccountsService {


    private final CurrencyRepo currencyRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CashAccountRepo cashAccountRepo;
    private final MobileBankingRepo mobileBankingRepo;

    public AddBankAccountResponseDto addBankAccount(User user, AddBankAccountRequestDto addBankAccountRequestDTO) {


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


    public AddMobileBankingResponseDto addMobileBankingAccount(User user, AddMobileBankingRequestDto addMobileBankingRequestDto) {

        if (mobileBankingRepo.existsByProviderNameAndPhoneNumber(addMobileBankingRequestDto.getProviderName(), addMobileBankingRequestDto.getPhoneNumber())) {
            throw new RuntimeException("phone already exists");
        }
        MobileBanking newAccount = MobileBanking.builder()
                .providerName(addMobileBankingRequestDto.getProviderName())
                .accountType(MobileBankingAccountType.valueOf(addMobileBankingRequestDto.getAccountType()))
                .phoneNumber(addMobileBankingRequestDto.getPhoneNumber())
                .currentBalance(addMobileBankingRequestDto.getCurrentBalance())
                .user(user)
                .build();

        mobileBankingRepo.save(newAccount);

        return AddMobileBankingResponseDto.builder()
                .id(newAccount.getId())
                .providerName(newAccount.getProviderName())
                .accountType(newAccount.getAccountType().toString())
                .phoneNumber(newAccount.getPhoneNumber())
                .currentBalance(newAccount.getCurrentBalance())
                .build();
    }


    public CreateCashAccountResponseDto addCashAccount(User user, CreateCashAccountRequestDto dto) {

        CashAccount newAccount = CashAccount.builder()
                .currentBalance(dto.getCurrentBalance())
                .currency(currencyRepo.findByCurrencyName(dto.getCurrency()).orElseThrow(() -> new RuntimeException("Currency does not exist")))
                .user(user)
                .build();
        cashAccountRepo.save(newAccount);

        return CreateCashAccountResponseDto.builder()
                .id(newAccount.getId())
                .currencyName(newAccount.getCurrency().getCurrencyName())
                .currentBalance(newAccount.getCurrentBalance())
                .build();

    }


    public OverallBalanceDto overallBalance(User user) {

        BankAccount bankAccount = bankAccountRepo.findByUser(user);
        CashAccount cashAccount = cashAccountRepo.findByUser(user).orElseThrow();
        MobileBanking mobileBanking= mobileBankingRepo.findByUser(user);


        return OverallBalanceDto.builder()
                .totalBalance(bankAccount.getCurrentBalance().add(mobileBanking.getCurrentBalance().add(cashAccount.getCurrentBalance())))
                .bankBalance(bankAccount.getCurrentBalance())
                .mobileBankingBalance(mobileBanking.getCurrentBalance())
                .cashBalance(cashAccount.getCurrentBalance())
                .build();


    }
}
