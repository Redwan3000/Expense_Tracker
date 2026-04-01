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
    private final PaymentMethodRepo paymentMethodRepo;
    private final UserRepo userRepo;

    public AddBankAccountResponseDto addBankAccount(User user, AddBankAccountRequestDto addBankAccountRequestDTO) {


        Bank newAccount = Bank.builder()
                .bankName(addBankAccountRequestDTO.getBankName())
                .bankBranch(addBankAccountRequestDTO.getBankBranch())
                .accountNumber(addBankAccountRequestDTO.getAccountNumber())
                .currency(currencyRepo.findByName(addBankAccountRequestDTO.getCurrency()).orElseThrow(() -> new RuntimeException("currency could not found")))
                .balance(addBankAccountRequestDTO.getCurrentBalance())
                .user(user)
                .build();
        bankAccountRepo.save(newAccount);

        return AddBankAccountResponseDto.builder()
                .id(newAccount.getId())
                .bankName(newAccount.getBankName())
                .bankBranch(newAccount.getBankBranch())
                .accountNumber(newAccount.getAccountNumber())
                .accountType(String.valueOf(newAccount.getAccountType()))
                .currencyName(newAccount.getCurrency().getName())
                .currentBalance(newAccount.getBalance())
                .build();

    }


    public AddMobileBankingResponseDto addMobileBankingAccount(User user, AddMobileBankingRequestDto addMobileBankingRequestDto) {

        if (mobileBankingRepo.existsByProviderNameAndPhoneNumber(addMobileBankingRequestDto.getProviderName(), addMobileBankingRequestDto.getPhoneNumber())) {
            throw new RuntimeException("phone already exists");
        }
        MobileBanking newAccount = MobileBanking.builder()
                .providerName(addMobileBankingRequestDto.getProviderName())
                .phoneNumber(addMobileBankingRequestDto.getPhoneNumber())
                .balance(addMobileBankingRequestDto.getCurrentBalance())
                .user(user)
                .build();

        mobileBankingRepo.save(newAccount);

        return AddMobileBankingResponseDto.builder()
                .id(newAccount.getId())
                .providerName(newAccount.getProviderName())
                .accountType(newAccount.getAccountType().toString())
                .phoneNumber(newAccount.getPhoneNumber())
                .currentBalance(newAccount.getBalance())
                .build();
    }


    public CreateCashAccountResponseDto addCashAccount(User user, CreateCashAccountRequestDto dto) {

        CashWallet newAccount = CashWallet.builder()
                .balance(dto.getBalance())
                .currency(currencyRepo.findByName(dto.getCurrency()).orElseThrow(() -> new RuntimeException("Currency does not exist")))
                .user(user)
                .build();
        cashAccountRepo.save(newAccount);

        return CreateCashAccountResponseDto.builder()
                .id(newAccount.getId())
                .currencyName(newAccount.getCurrency().getName())
                .currentBalance(newAccount.getBalance())
                .build();

    }


    public OverallBalanceDto overallBalance(User user) {

//        BankAccount bankAccount = bankAccountRepo.findByUser(user);
//        CashAccount cashAccount = cashAccountRepo.findByUser(user).orElseThrow();
//        MobileBanking mobileBanking = mobileBankingRepo.findByUser(user);



        OverallBalanceDto allAccountBalanceSheet= userRepo.fetchAccountsBalance(user.getId());

        return allAccountBalanceSheet;


    }



    public ModifyBankAccountDetailsResponseDto modifyBankAccountDetails(User user, long id, ModifyBankAccountDetailsRequestDto dto) {

        Bank account = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

        account.setAccountNumber(dto.getAccountNumber() == null ? account.getAccountNumber() : dto.getAccountNumber());
        account.setCurrency(dto.getAccountNumber() == null ? account.getCurrency() : currencyRepo.findByName(dto.getCurrencyId()).orElseThrow(() -> new RuntimeException("invalid currency")));
        account.setBankName(dto.getBankName() == null ? account.getBankName() : dto.getBankName());
        account.setBalance(dto.getBalance() == null ? account.getBalance() : dto.getBalance());


        bankAccountRepo.save(account);
        return ModifyBankAccountDetailsResponseDto.builder()
                .bankId(account.getId())
                .accountNumber(account.getAccountNumber())
                .currencyId(account.getCurrency().getName())
                .bankName(account.getBankName())
                .bankBranch(account.getBankBranch())
                .accountType(account.getAccountType().toString())
                .balance(account.getBalance())
                .build();
    }




    public ModifyMobileBankingDetailsResponseDto modifyMobileBankingAccountDetails(User user, long id, ModifyBankAccountDetailsRequestsDTo Dto) {

        MobileBanking mobileBankAccount = mobileBankingRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("mobile banking account not found"));

        mobileBankAccount.setBalance(Dto.getCurrentBalance() == null ? mobileBankAccount.getBalance() : Dto.getCurrentBalance());
        mobileBankAccount.setPhoneNumber(Dto.getPhoneNumber() == null ? mobileBankAccount.getPhoneNumber() : Dto.getPhoneNumber());
        mobileBankAccount.setProviderName(Dto.getProviderName() == null ? mobileBankAccount.getProviderName() : Dto.getProviderName());

        mobileBankingRepo.save(mobileBankAccount);
        return ModifyMobileBankingDetailsResponseDto.builder()
                .id(mobileBankAccount.getId())
                .providerName(mobileBankAccount.getProviderName())
                .accountType(mobileBankAccount.getAccountType().toString())
                .phoneNumber(mobileBankAccount.getPhoneNumber())
                .currentBalance(mobileBankAccount.getBalance())
                .build();
    }

    @Transactional
    public AddBankAccountResponseDto deleteBankAccount(User user, long id) {

        List<Transactions> deletedAccountTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(2).orElseThrow(() -> new RuntimeException("transaction method not found")), id);


        Bank bank = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

        AddBankAccountResponseDto response = AddBankAccountResponseDto.builder()
                .id(bank.getId())
                .bankName(bank.getBankName())
                .bankBranch(bank.getBankBranch())
                .accountNumber(bank.getAccountNumber())
                .accountType(bank.getAccountType().toString())
                .currencyName(bank.getCurrency().getName())
                .currentBalance(bank.getBalance())
                .build();

        deletedAccountTransactions.forEach(e -> transactionRepo.softDeleteTransactions(e.getId()));
        bankAccountRepo.softDeleteBankAccount(bank.getId());

        return response;

    }

    @Transactional
    public AddMobileBankingResponseDto deleteMobileBankingAccount(User user, long id) {

        List<Transactions> deletedAccountTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(2).orElseThrow(() -> new RuntimeException("transaction method not found")), id);


        MobileBanking mobileBankingAccount = mobileBankingRepo.findByUserAndId(user, id).orElseThrow(()->new RuntimeException("mobile banking not found"));

        AddMobileBankingResponseDto response = AddMobileBankingResponseDto.builder()
                .id(mobileBankingAccount.getId())
                .providerName(mobileBankingAccount.getProviderName())
                .accountType(mobileBankingAccount.getAccountType().toString())
                .phoneNumber(mobileBankingAccount.getPhoneNumber())
                .currentBalance(mobileBankingAccount.getBalance())
                .build();

        deletedAccountTransactions.forEach(e -> transactionRepo.softDeleteTransactions(e.getId()));
        mobileBankingRepo.softDeleteMobileBankingAccount(mobileBankingAccount.getId());

        return response;
    }

    @Transactional
    public AllAccountBalanceDto getAccountsBalance(User user) {

return new AllAccountBalanceDto();
    }
}
