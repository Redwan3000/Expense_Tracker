package com.arits.expense_trancker.service;

import com.arits.expense_trancker.Mapper.AccountMapper;
import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.dto.AccountDetailsDto;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.handler.EntityProvider;
import com.arits.expense_trancker.handler.Resolver;
import com.arits.expense_trancker.handler.Verifier;
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
    private final TransactionRepo transactionRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final UserRepo userRepo;
    private final AccountTypeRepo accountTypeRepo;
    private final ProviderRepo providerRepo;
    private final AccountRepo accountRepo;
    private final BalanceRepo balanceRepo;
    private final Verifier helper;
    private final Resolver resolver;
    private final AccountMapper accountMapper;
    private final EntityProvider entityProvider;

    public AccountType accountTypeSeeding(String accountTypeName) {

        return accountTypeRepo.findByNameIgnoreCase(accountTypeName).orElseGet(() -> {

            return accountTypeRepo.save(AccountType.builder()
                    .name(accountTypeName)
                    .build());
        });
    }


    //Service for Users to add account

    @Transactional
    public AccountResponseDto addAccount(User user, AddAccountRequestDto dto, Long userId, Long subuserId) {

        User targetUser = resolver.getTargetUser(user, userId, subuserId);

        if (accountRepo.existsByUserIdAndAccountNumber(targetUser.getId(), dto.getAccountNumber())) {
            throw new RuntimeException("account already exist");
        }


        Currency currency = currencyRepo.findById(dto.getCurrency()).orElseThrow(() -> new RuntimeException("Invalid Currency"));

        AccountType accountType = accountTypeRepo.findById(dto.getAccountType()).orElseThrow(() -> new RuntimeException("invalid Account Type"));

        PaymentMethod paymentMethod = paymentMethodRepo.findById(dto.getPaymentMethod()).orElseThrow(() -> new RuntimeException("invalid "));

        Provider provider = providerRepo.findById(dto.getProvider()).orElseThrow(() -> new RuntimeException("invalid provider"));

        Account newAccount = Account.builder()
                .currency(currency)
                .user(targetUser)
                .accountType(accountType)
                .paymentMethod(paymentMethod)
                .build();

        AccountDetails accountDetails = AccountDetails.builder()
                .accountNumber(dto.getAccountNumber())
                .nomineeName(dto.getNomineeName())
                .provider(provider)
                .accountHolder(dto.getAccountHolder())
                .account(newAccount).build();


        Balance currentBalance = Balance.builder()
                .amount(dto.getBalance())
                .account(newAccount)
                .build();

        newAccount.setAccountDetails(accountDetails);
        newAccount.setBalance(currentBalance);

        accountRepo.save(newAccount);

        return accountMapper.toDto(newAccount);

    }


    @Transactional
    public AccountResponseDto modifyAccountDetails(User user, ModifyAccountDetailsRequestDto dto, Long accountId, Long userId, Long subuserId) {

        User targetUser = resolver.getTargetUser(user, userId, subuserId);

        Account accountToModify = accountRepo.findByUserIdAndId(targetUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belong to this User"));

        accountToModify.setAccountType(entityProvider.resolveAccountType(dto.getAccountType(), accountToModify.getAccountType()));
        accountToModify.getAccountDetails().setAccountNumber(entityProvider.resolveString(dto.getAccountNumber(), accountToModify.getAccountDetails().getAccountNumber()));
        accountToModify.getAccountDetails().setAccountHolder(entityProvider.resolveString(dto.getAccountHolder(), accountToModify.getAccountDetails().getAccountHolder()));
        accountToModify.getAccountDetails().setNomineeName(entityProvider.resolveString(dto.getNomineeName(), accountToModify.getAccountDetails().getNomineeName()));
        accountToModify.setCurrency(entityProvider.resolveCurrency(dto.getCurrency(), accountToModify.getCurrency()));
        accountToModify.getBalance().setAmount(entityProvider.resolveBalance(dto.getBalance(), accountToModify.getBalance().getAmount()));

        accountRepo.save(accountToModify);

        return accountMapper.toDto(accountToModify);

    }


    //Service to Get the Account Balance Group Wise
    @Transactional
    public AllAccountDetails getAccountsBalance(User user) {

        List<AccountDetailsDto> allAccount = paymentMethodRepo.getAllAccountBalance(user.getId());

        Map<String, List<AccountDetailsDto>> groupedBalance = allAccount
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
    public DeleteAccountDto deleteAccount(Long systemUserId, Long accountId, Long userId, Long subuserId) {

        Long mainUser= resolver.getTargetUserId(systemUserId,userId,subuserId);

        Account accountToDelete= accountRepo.findByUserIdAndId(mainUser,accountId).orElseThrow(()->new RuntimeException("Account does not belong to user"));


        List<Transactions> deletedAccountTransactions = transactionRepo.findByAccountId(accountId);
        transactionRepo.deleteAll(deletedAccountTransactions);
        accountRepo.delete(accountToDelete);

        return DeleteAccountDto.builder()
                .accountId(accountId)
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
    public AllAccountDetails getAllAccounts(Long userId) {

        List<AccountDetailsDto> allAccount = accountRepo.findByUserId(userId);

        Map<String, List<AccountDetailsDto>> groupedAccounts = allAccount.stream()
                .collect(Collectors.groupingBy(a -> a.getPaymentMethod() != null ? a.getPaymentMethod().toUpperCase() : "UNKNOWN"));

        return AllAccountDetails.builder()
                .totalBalance(balanceRepo.getAllAccountBalance(userId))
                .bankAccounts(groupedAccounts.getOrDefault("BANK", List.of()))
                .mobileBankingAccounts(groupedAccounts.getOrDefault("MOBILE_BANKING", List.of()))
                .cashWallets(groupedAccounts.getOrDefault("CASH", List.of()))
                .build();

    }
}

