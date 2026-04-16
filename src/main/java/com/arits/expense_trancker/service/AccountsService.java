package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.dto.AccountDetails;
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
    private final BalanceRepo balanceRepo;


    public AccountType accountTypeSeeding(String accountTypeName) {

        return accountTypeRepo.findByNameIgnoreCase(accountTypeName).orElseGet(() -> {

            return accountTypeRepo.save(AccountType.builder()
                    .name(accountTypeName)
                    .build());
        });
    }


    @Transactional
    public AddAccountResponseDto addAccount(User user, AddAccountRequestDto dto,Long userId,Long subuserId) {

        if (accountRepo.existsByUserIdAndAccountNumber(userId, dto.getAccountNumber())) {
            throw new RuntimeException("account already exist");
        }

        Currency currency = currencyRepo.findById(dto.getCurrency()).orElseThrow(() -> new RuntimeException("Invalid Currency"));

        AccountType accountType = accountTypeRepo.findById(dto.getAccountType()).orElseThrow(() -> new RuntimeException("invalid Account Type"));

        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethod()).orElseThrow(() -> new RuntimeException("invalid "));

        ProviderList provider = providerListRepo.findById(dto.getProvider()).orElseThrow(() -> new RuntimeException("invalid provider"));
        Account newAccount = Account.builder()
                .createdAt(LocalDateTime.now())
                .currency(currency)
                .user(user)
                .accountType(accountType)
                .paymentMethod(paymentMethod)
                .build();

        com.arits.expense_trancker.entity.AccountDetails accountdetails = com.arits.expense_trancker.entity.AccountDetails.builder()
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


    @Transactional
    public AddAccountResponseDto modifyAccountDetails(ModifyAccountDetailsRequestDto requestDTo, Long accountId) {

        Account accountToModify = accountRepo.findById(accountId).orElseThrow(() -> new RuntimeException("account does not exist"));

        accountToModify.setAccountType(requestDTo.getAccountType() != null ? accountTypeRepo.findById(requestDTo.getAccountType()).orElseThrow(() -> new RuntimeException("invalid AccountType")) : accountToModify.getAccountType());
        accountToModify.getAccountDetails().setAccountNumber(requestDTo.getAccountNumber() != null ? requestDTo.getAccountNumber() : accountToModify.getAccountDetails().getAccountNumber());
        accountToModify.getAccountDetails().setAccountHolder(requestDTo.getAccountHolder() != null ? requestDTo.getAccountHolder() : accountToModify.getAccountDetails().getAccountHolder());
        accountToModify.getAccountDetails().setNomineeName(requestDTo.getNomineeName() != null ? requestDTo.getNomineeName() : accountToModify.getAccountDetails().getNomineeName());
        accountToModify.setCurrency(currencyRepo.findById(requestDTo.getCurrency()).orElseThrow(() -> new RuntimeException("invalid CurrencyId")));
        accountToModify.getBalance().setBalance(requestDTo.getBalance() != null ? requestDTo.getBalance() : accountToModify.getBalance().getBalance());

        accountRepo.save(accountToModify);

        return AddAccountResponseDto.builder()
                .accountId(accountToModify.getId())
                .currency(accountToModify.getCurrency().getName())
                .accountType(accountToModify.getAccountType().getName())
                .paymentMethod(accountToModify.getPaymentMethod().getName())
                .accountNumber(accountToModify.getAccountDetails().getAccountNumber())
                .nomineeName(accountToModify.getAccountDetails().getNomineeName())
                .providerName(accountToModify.getAccountDetails().getProvider().getName())
                .currentBalance(accountToModify.getBalance().getBalance())
                .AccountHolder(accountToModify.getAccountDetails().getAccountHolder())
                .createdAt(accountToModify.getCreatedAt())
                .build();

    }


    //Service to Get the Account Balance Group Wise
    @Transactional
    public AllAccountDetails getAccountsBalance(User user) {

        List<AccountDetails> allAccount = paymentMethodRepo.getAllAccountBalance(user.getId());

        Map<String, List<AccountDetails>> groupedBalance = allAccount
                .stream()
                .collect(Collectors.
                        groupingBy(a -> a.getPaymentMethod().toUpperCase()));

        groupedBalance.forEach((key, value) -> System.out.println(">>> key: " + key + " size: " + value.size()));

        return AllAccountDetails.builder()
                .totalBalance(userRepo.getAllAccountBalance(user.getId()))
                .bankAccounts(groupedBalance.getOrDefault("BANK", List.of()))
                .mobileBankingAccounts(groupedBalance.getOrDefault("MOBILE_BANKING", List.of()))
                .cashAccounts(groupedBalance.getOrDefault("CASH", List.of()))
                .build();
    }


    //    service level to delete any Account
    @Transactional
    public DeleteAccountDto deleteAccount(Account validAccount) {

        List<Transactions> deletedAccountTransactions = transactionRepo.findByAccountId(validAccount.getId());
        transactionRepo.deleteAll(deletedAccountTransactions);
        accountRepo.delete(validAccount);

        return DeleteAccountDto.builder()
                .accountId(validAccount.getId())
                .paymentType(validAccount.getPaymentMethod().getName())
                .deletedStatus(true)
                .build();

    }


    //    service level for getting Account details
    public AccountDetailsResponseDto getAccountDetials(Account validAccount) {

        return AccountDetailsResponseDto.builder()
                .accountId(validAccount.getId())
                .currency(validAccount.getCurrency().getName())
                .accountType(validAccount.getAccountType().getName())
                .paymentMethod(validAccount.getPaymentMethod().getName())
                .accountNumber(validAccount.getAccountDetails().getAccountNumber())
                .nomineeName(validAccount.getAccountDetails().getNomineeName())
                .providerName(validAccount.getAccountDetails().getProvider().getName())
                .currentBalance(validAccount.getBalance().getBalance())
                .lastBalanceUpdatedAt(validAccount.getBalance().getUpdatedAt())
                .AccountHolder(validAccount.getAccountDetails().getAccountHolder())
                .createdAt(validAccount.getCreatedAt())
                .updatedAt(validAccount.getUpdatedAt())
                .build();
    }


    @Transactional
    public AllAccountDetails getAllAccounts(Long userId){

        List<AccountDetails> allAccount= accountRepo.findByUserId(userId);

    Map<String , List<AccountDetails>> groupedAccounts=allAccount.stream()
            .collect(Collectors.groupingBy(a -> a.getPaymentMethod() != null ? a.getPaymentMethod().toUpperCase() : "UNKNOWN"));

    return AllAccountDetails.builder()
            .totalBalance(balanceRepo.getAllAccountBalance(userId))
            .bankAccounts(groupedAccounts.getOrDefault("BANK", List.of()))
            .mobileBankingAccounts(groupedAccounts.getOrDefault("MOBILE_BANKING",List.of()))
            .cashWallets(groupedAccounts.getOrDefault("CASH",List.of()))
            .build();

    }
}

