package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountsService {


    private final CurrencyRepo currencyRepo;
    private final TransactionRepo transactionRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final UserRepo userRepo;
    private final AccountTypeRepo accountTypeRepo;
    private final ProviderListRepo providerListRepo;
    private final AccountRepo accountRepo;


    @Transactional
    public AddAccountResponseDto addAccount(User user, AddAccountRequestDto requestDto) {
        if (accountRepo.existsByUserIdAndAccountNumber(user.getId(), requestDto.getAccountNumber())) {
            throw new RuntimeException("account already exist");
        }

        Currency currency = currencyRepo.findById(requestDto.getCurrency()).orElseThrow(() -> new RuntimeException("Invalid Currency"));

        AccountType accountType = accountTypeRepo.findById(requestDto.getAccountType()).orElseThrow(() -> new RuntimeException("invalid Account Type"));

        PaymentMethod paymentMethod = paymentMethodRepo.findById(requestDto.getPaymentMethod()).orElseThrow(() -> new RuntimeException("invalid "));

        ProviderList provider = providerListRepo.findById(requestDto.getProvider()).orElseThrow(() -> new RuntimeException("invalid provider"));
        Account newAccount = Account.builder()
                .createdAt(LocalDateTime.now())
                .currency(currency)
                .user(user)
                .accountType(accountType)
                .paymentMethod(paymentMethod)
                .build();

        AccountDetails accountdetails = AccountDetails.builder()
                .accountNumber(requestDto.getAccountNumber())
                .nomineeName(requestDto.getNomineeName())
                .createdAt(LocalDateTime.now())
                .provider(provider)
                .accountHolder(requestDto.getAccountHolder())
                .account(newAccount).build();


        Balance currentBalance = Balance.builder()
                .balance(requestDto.getBalance())
                .createdAt(LocalDateTime.now())
                .account(newAccount)
                .build();

        newAccount.setAccountDetails(accountdetails);
        newAccount.setBalance(currentBalance);
        newAccount.setCreatedAt(LocalDateTime.now());
        newAccount.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(newAccount);

        return AddAccountResponseDto.builder()
                .accountId(newAccount.getId())
                .currency(newAccount.getCurrency().getName())
                .accountType(newAccount.getAccountType().getName())
                .paymentMethod(newAccount.getPaymentMethod().getName())
                .accountNumber(newAccount.getAccountDetails().getAccountNumber())
                .nomineeName(newAccount.getAccountDetails().getNomineeName())
                .providerName(newAccount.getAccountDetails().getProvider().getName())
                .currentBalance(newAccount.getBalance().getBalance())
                .AccountHolder(newAccount.getAccountDetails().getAccountHolder())
                .createdAt(newAccount.getCreatedAt())
                .build();

    }


    public AccountType accountTypeSeeding(String accountTypeName) {

        return accountTypeRepo.findByNameIgnoreCase(accountTypeName).orElseGet(() -> {

            return accountTypeRepo.save(AccountType.builder()
                    .name(accountTypeName)
                    .build());
        });
    }


    public ModifyBankAccountDetailsResponseDto modifyBankAccountDetails(User user, long id, ModifyBankAccountDetailsRequestDto dto) {

        BankList account = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

        account.setAccountNumber(dto.getAccountNumber() == null ? account.getAccountNumber() : dto.getAccountNumber());
        account.setBankName(dto.getBankName() == null ? account.getBankName() : dto.getBankName());
        account.setBankBranch(dto.getBankBranch() == null ? account.getBankBranch() : dto.getBankBranch());
        account.setBalance(dto.getBalance() == null ? account.getBalance() : dto.getBalance());
        account.setCurrency(dto.getAccountNumber() == null ? account.getCurrency() : currencyRepo.findByNameIgnoreCase(dto.getCurrencyId()).orElseThrow(() -> new RuntimeException("invalid currency")));
        account.setAccountType(dto.getAccountType() == null ? account.getAccountType() : accountTypeRepo.findByNameIgnoreCase(dto.getAccountType()).orElseThrow(() -> new RuntimeException("Account Type Not Found")));


        bankAccountRepo.save(account);
        return ModifyBankAccountDetailsResponseDto.builder()
                .bankId(account.getId())
                .accountNumber(account.getAccountNumber())
                .currencyId(account.getCurrency().getName())
                .bankName(account.getBankName())
                .bankBranch(account.getBankBranch())
                .accountType(account.getAccountType().getName())
                .balance(account.getBalance())
                .build();
    }


    public ModifyMobileBankingDetailsResponseDto modifyMobileBankingAccountDetails(User user, long id, ModifyBankAccountDetailsRequestsDTo dto) {

        MobileBankingList mobileBankAccount = mobileBankingRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("mobile banking account not found"));

        mobileBankAccount.setBalance(dto.getCurrentBalance() == null ? mobileBankAccount.getBalance() : dto.getCurrentBalance());
        mobileBankAccount.setPhoneNumber(dto.getPhoneNumber() == null ? mobileBankAccount.getPhoneNumber() : dto.getPhoneNumber());
        mobileBankAccount.setProviderName(dto.getProviderName() == null ? mobileBankAccount.getProviderName() : dto.getProviderName());
        mobileBankAccount.setAccountType(dto.getAccountType() == null ? mobileBankAccount.getAccountType() : accountTypeRepo.findByNameIgnoreCase(dto.getAccountType()).orElseThrow(() -> new RuntimeException("Account Type Not Found")));

        mobileBankingRepo.save(mobileBankAccount);

        return ModifyMobileBankingDetailsResponseDto.builder()
                .id(mobileBankAccount.getId())
                .providerName(mobileBankAccount.getProviderName())
                .accountType(mobileBankAccount.getAccountType().getName())
                .phoneNumber(mobileBankAccount.getPhoneNumber())
                .currentBalance(mobileBankAccount.getBalance())
                .build();
    }


    public CreateCashAccountResponseDto modifyCashWallet(User user, ModifyCashWalletDetailsDto dto) {

        CashWalletDetails cashWalletDetails = cashWalletRepo.findByUserId(user.getId()).orElseThrow(() -> new RuntimeException("Users Cash Wallet Not Found"));

        cashWalletDetails.setBalance(dto.getBalance() == null ? cashWalletDetails.getBalance() : dto.getBalance());
        cashWalletDetails.setCurrency(dto.getCurrency() == null ? cashWalletDetails.getCurrency() : currencyRepo.findByNameIgnoreCase(dto.getCurrency()).orElseThrow(() -> new RuntimeException("invalid currency")));
        cashWalletRepo.save(cashWalletDetails);

        return CreateCashAccountResponseDto.builder()
                .id(cashWalletDetails.getId())
                .currencyName(cashWalletDetails.getCurrency().getName())
                .currentBalance(cashWalletDetails.getBalance())
                .paymentMethod(cashWalletDetails.getPaymentMethod().getName())
                .build();


    }


    @Transactional
    public DeleteAccountDto deleteCashWallet(Long userId) {

        CashWalletDetails cashWalletDetails = cashWalletRepo.findByUserId(userId).orElseThrow(() -> new RuntimeException("Users wallet not found"));
        List<Transactions> deletedWalletTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(1L).orElseThrow(() -> new RuntimeException("transaction method not found")), cashWalletDetails.getId());

        transactionRepo.deleteAll(deletedWalletTransactions);
        cashWalletRepo.delete(cashWalletDetails);
        return DeleteAccountDto.builder()
                .accountId(cashWalletDetails.getId())
                .paymentType(cashWalletDetails.getPaymentMethod().getName())
                .deletedStatus(true)
                .build();
    }


    @Transactional
    public DeleteAccountDto deleteBankAccount(User user, long id) {

        BankList bank = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));
        List<Transactions> deletedAccountTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(2L).orElseThrow(() -> new RuntimeException("transaction method not found")), id);

        transactionRepo.deleteAll(deletedAccountTransactions);
        bankAccountRepo.delete(bank);


        return DeleteAccountDto.builder()
                .accountId(bank.getId())
                .paymentType(bank.getPaymentMethod().getName())
                .deletedStatus(true)
                .build();

    }

    @Transactional
    public DeleteAccountDto deleteMobileBankingAccount(Long userId, Long accountId) {

        MobileBankingList mobileBankingListAccount = mobileBankingRepo.findByUserIdAndAccountId(userId, accountId).orElseThrow(() -> new RuntimeException("mobile banking not found"));


        List<Transactions> deletedAccountTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(3L).orElseThrow(() -> new RuntimeException("transaction method not found")), accountId);

        transactionRepo.deleteAll(deletedAccountTransactions);
        mobileBankingRepo.delete(mobileBankingListAccount);

        return DeleteAccountDto.builder()
                .accountId(mobileBankingListAccount.getId())
                .paymentType(mobileBankingListAccount.getPaymentMethod().getName())
                .deletedStatus(true)
                .build();
    }


    //Service to Get the Account Balance Group Wise
    @Transactional
    public AllAccountBalanceDto getAccountsBalance(User user) {

        List<AccountBalanceResponseDto> allAccount = paymentMethodRepo.getAllAccountBalance(user.getId());

        Map<String, List<AccountBalanceResponseDto>> groupedBalance = allAccount
                .stream()
                .collect(Collectors.
                        groupingBy(a -> a.getPaymentMethod().toUpperCase()));

        groupedBalance.forEach((key, value) -> System.out.println(">>> key: " + key + " size: " + value.size()));

        return AllAccountBalanceDto.builder()
                .totalBalance(userRepo.getAllAccountBalance(user.getId()))
                .bankAccounts(groupedBalance.getOrDefault("BANK", List.of()))
                .mobileBankingAccounts(groupedBalance.getOrDefault("MOBILE_BANKING", List.of()))
                .cashAccounts(groupedBalance.getOrDefault("CASH", List.of()))
                .build();
    }


    //Service method for bank account details through accountId
    public List<BankAccountsDetailDto> getBankAccountDetails(User user, Long accountId) {

        List<BankAccountsDetailDto> response = bankAccountRepo.getAccountDetails(user.getId(), accountId);

        if (response.isEmpty()) {
            throw new RuntimeException("Bank Account Detail Not Found");
        }

        return response;
    }


    //Service method for mobile banking account details through accountId
    public List<MobileAccountsDetailsDto> getMobileBankingAccountDetails(Long userId, Long accountId) {

        return mobileBankingRepo.getAccountDetials(userId, accountId);

    }


    //Service method for Cash wallet account detail
    public CashWalletDetailsDto getCashWalletAccountDetails(User user) {

        return cashWalletRepo.getCashWalletDetails(user.getId()).orElseThrow(() -> new RuntimeException("Cash Wallet Not Found"));

    }

    public AddAccountResponseDto  modifyAccountDetails(Long userId, ModifyAccountDetailsRequestDto requestDTo,Long paymentMethod,Long accountId) {


    }
}

