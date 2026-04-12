package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountsService {


    private final CurrencyRepo currencyRepo;
    private final BankAccountRepo bankAccountRepo;
    private final CashWalletRepo cashWalletRepo;
    private final MobileBankingRepo mobileBankingRepo;
    private final TransactionRepo transactionRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final UserRepo userRepo;
    private final AccountTypeRepo accountTypeRepo;


    @Transactional
    public CreateCashAccountResponseDto addCashAccount(User user, CreateCashAccountRequestDto dto) {

        if (cashWalletRepo.existsByUserId(user.getId())) {
            throw new RuntimeException("Users Wallet Already Exist");
        }

        CashWalletDetails cashWalletDetails = cashWalletRepo.save(CashWalletDetails.builder()
                .balance(dto.getBalance())
                .currency(currencyRepo.findByNameIgnoreCase(dto.getCurrency()).orElseThrow(() -> new RuntimeException("currency not found")))
                .user(user)
                .paymentMethod(paymentMethodRepo.findById(1L).orElseThrow(() -> new RuntimeException("payment method not found")))
                .build());

        return CreateCashAccountResponseDto.builder()
                .id(cashWalletDetails.getId())
                .currencyName(cashWalletDetails.getCurrency().getName())
                .currentBalance(cashWalletDetails.getBalance())
                .paymentMethod(cashWalletDetails.getPaymentMethod().getName())
                .build();

    }


    public AddBankAccountResponseDto addBankAccount(User user, AddBankAccountRequestDto requestDto) {
        if (bankAccountRepo.existsByUserIdAndAccountNumber(user.getId(), requestDto.getAccountNumber())) {
            throw new RuntimeException("account already exist");
        }
        Banks newAccount = Banks.builder()
                .accountNumber(requestDto.getAccountNumber())
                .bankName(requestDto.getBankName())
                .bankBranch(requestDto.getBankBranch())
                .balance(requestDto.getCurrentBalance())
                .currency(currencyRepo.findByNameIgnoreCase(requestDto.getCurrency()).orElseThrow(() -> new RuntimeException("currency not found")))
                .user(user)
                .accountType(accountTypeRepo.findByNameIgnoreCase(requestDto.getAccountType()).orElseThrow(() -> new RuntimeException("account type not found")))
                .paymentMethod(paymentMethodRepo.findById(2L).orElseThrow(() -> new RuntimeException("paymentMethod not found")))
                .build();

        bankAccountRepo.save(newAccount);

        return AddBankAccountResponseDto.builder()
                .id(newAccount.getId())
                .bankName(newAccount.getBankName())
                .bankBranch(newAccount.getBankBranch())
                .accountNumber(newAccount.getAccountNumber())
                .accountType(newAccount.getAccountType().getName())
                .currencyName(newAccount.getCurrency().getName())
                .currentBalance(newAccount.getBalance())
                .build();

    }


    public AddMobileBankingResponseDto addMobileBankingAccount(User user, AddMobileBankingRequestDto dto) {

        if (mobileBankingRepo.existsByProviderNameAndPhoneNumber(dto.getProviderName(), dto.getPhoneNumber())) {
            throw new RuntimeException("phone already exists");
        }
        MobileBanks newAccount = MobileBanks.builder()
                .providerName(dto.getProviderName())
                .phoneNumber(dto.getPhoneNumber())
                .balance(dto.getCurrentBalance())
                .user(user)
                .currency(currencyRepo.findByNameIgnoreCase(dto.getCurrency()).orElseThrow(() -> new RuntimeException("currency not found")))
                .accountType(accountTypeRepo.findByNameIgnoreCase(dto.getAccountType()).orElseThrow(() -> new RuntimeException("account type not found")))
                .paymentMethod(paymentMethodRepo.findById(3L).orElseThrow(() -> new RuntimeException("paymentMethod not found")))
                .build();
        mobileBankingRepo.save(newAccount);

        return AddMobileBankingResponseDto.builder()
                .id(newAccount.getId())
                .providerName(newAccount.getProviderName())
                .accountType(newAccount.getAccountType().getName())
                .phoneNumber(newAccount.getPhoneNumber())
                .currentBalance(newAccount.getBalance())
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

        Banks account = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));

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

        MobileBanks mobileBankAccount = mobileBankingRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("mobile banking account not found"));

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

        Banks bank = bankAccountRepo.findByUserAndId(user, id).orElseThrow(() -> new RuntimeException("bank account not found"));
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

        MobileBanks mobileBanksAccount = mobileBankingRepo.findByUserIdAndAccountId(userId, accountId).orElseThrow(() -> new RuntimeException("mobile banking not found"));


        List<Transactions> deletedAccountTransactions = transactionRepo.findByPaymentMethodAndAccountId(paymentMethodRepo.findByMethodId(3L).orElseThrow(() -> new RuntimeException("transaction method not found")), accountId);

        transactionRepo.deleteAll(deletedAccountTransactions);
        mobileBankingRepo.delete(mobileBanksAccount);

        return DeleteAccountDto.builder()
                .accountId(mobileBanksAccount.getId())
                .paymentType(mobileBanksAccount.getPaymentMethod().getName())
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
}

