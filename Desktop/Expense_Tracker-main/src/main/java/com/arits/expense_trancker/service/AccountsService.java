package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AccountsService {


    private final CurrencyRepo currencyRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CashAccountRepo cashAccountRepo;
    private final MobileBankingRepo mobileBankingRepo;
    private final TransactionRepo transactionRepo;
    private final TransactionMethodRepo transactionMethodRepo;

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
        MobileBanking mobileBanking = mobileBankingRepo.findByUser(user);


        return OverallBalanceDto.builder()
                .totalBalance(bankAccount.getCurrentBalance().add(mobileBanking.getCurrentBalance().add(cashAccount.getCurrentBalance())))
                .bankBalance(bankAccount.getCurrentBalance())
                .mobileBankingBalance(mobileBanking.getCurrentBalance())
                .cashBalance(cashAccount.getCurrentBalance())
                .build();


    }

    public ModifyBankAccountDetailsResponseDto modifyBankAccountDetails(User user, long id, ModifyBankAccountDetailsRequestDto dto) {

        BankAccount account = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

        account.setAccountNumber(dto.getAccountNumber() == null ? account.getAccountNumber() : dto.getAccountNumber());
        account.setAccountType(dto.getAccountNumber() == null ? BankAccountType.valueOf(account.getAccountType().toString()) : BankAccountType.valueOf(dto.getAccountType()));
        account.setCurrency(dto.getAccountNumber() == null ? account.getCurrency() : currencyRepo.findByCurrencyName(dto.getCurrencyId()).orElseThrow(() -> new RuntimeException("invalid currency")));
        account.setBankName(dto.getBankName() == null ? account.getBankName() : dto.getBankName());
        account.setCurrentBalance(dto.getBalance() == null ? account.getCurrentBalance() : dto.getBalance());


        bankAccountRepo.save(account);
        return ModifyBankAccountDetailsResponseDto.builder()
                .bankId(account.getId())
                .accountNumber(account.getAccountNumber())
                .currencyId(account.getCurrency().getCurrencyName())
                .bankName(account.getBankName())
                .bankBranch(account.getBankBranch())
                .accountType(account.getAccountType().toString())
                .balance(account.getCurrentBalance())
                .build();
    }

    public ModifyMobileBankingDetailsResponseDto modifyMobileBankingAccountDetails(User user, long id, ModifyMobileBankingDetailsRequestDto Dto) {

        MobileBanking mobileBankAccount = mobileBankingRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("mobile banking account not found"));

        mobileBankAccount.setCurrentBalance(Dto.getCurrentBalance() == null ? mobileBankAccount.getCurrentBalance() : Dto.getCurrentBalance());
        mobileBankAccount.setPhoneNumber(Dto.getPhoneNumber() == null ? mobileBankAccount.getPhoneNumber() : Dto.getPhoneNumber());
        mobileBankAccount.setProviderName(Dto.getProviderName() == null ? mobileBankAccount.getProviderName() : Dto.getProviderName());
        mobileBankAccount.setAccountType(Dto.getAccountType() == null ? MobileBankingAccountType.valueOf(mobileBankAccount.getAccountType().toString()) : MobileBankingAccountType.valueOf(Dto.getAccountType()));

        mobileBankingRepo.save(mobileBankAccount);
        return ModifyMobileBankingDetailsResponseDto.builder()
                .id(mobileBankAccount.getId())
                .providerName(mobileBankAccount.getProviderName())
                .accountType(mobileBankAccount.getAccountType().toString())
                .phoneNumber(mobileBankAccount.getPhoneNumber())
                .currentBalance(mobileBankAccount.getCurrentBalance())
                .build();
    }

    @Transactional
    public AddBankAccountResponseDto deleteBankAccount(User user, long id) {

        List<Transactions> deletedAccountTransactions= transactionRepo.findByTransactionMethodsAndAccountId(transactionMethodRepo.findByMethodId(2).orElseThrow(()->new RuntimeException("transaction method not found")),id);


        BankAccount bankAccount = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

        AddBankAccountResponseDto response = AddBankAccountResponseDto.builder()
                .id(bankAccount.getId())
                .bankName(bankAccount.getBankName())
                .bankBranch(bankAccount.getBankBranch())
                .accountNumber(bankAccount.getAccountNumber())
                .accountType(bankAccount.getAccountType().toString())
                .currencyName(bankAccount.getCurrency().getCurrencyName())
                .currentBalance(bankAccount.getCurrentBalance())
                .build();

        deletedAccountTransactions.forEach(e->transactionRepo.softDeleteTransactions(e.getTransactionId()));
        bankAccountRepo.softDeleteBankAccount(bankAccount.getId());

        return response;

    }


}
