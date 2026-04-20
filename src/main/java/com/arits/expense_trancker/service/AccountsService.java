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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountsService {


    private final CurrencyRepo currencyRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final AccountTypeRepo accountTypeRepo;
    private final ProviderRepo providerRepo;
    private final AccountRepo accountRepo;
    private final BalanceRepo balanceRepo;
    private final Resolver resolver;
    private final AccountMapper accountMapper;
    private final EntityProvider entityProvider;
    private final TransactionRepo transactionRepo;
    private final AccountDetailsRepo accountDetailsRepo;
    private final Verifier verifier;

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

        return accountMapper.toAccountResponseDto(newAccount);

    }


    //    service level to mopdify Account
    @Transactional
    public AccountResponseDto modifyAccountDetails(User user, ModifyAccountDetailsRequestDto dto, Long userId, Long subuserId, Long accountId) {

        User targetUser = resolver.getTargetUser(user, userId, subuserId);

        Account accountToModify = accountRepo.findByUserIdAndId(targetUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belong to this User"));

        accountToModify.setAccountType(entityProvider.resolveAccountType(dto.getAccountType(), accountToModify.getAccountType()));
        accountToModify.getAccountDetails().setAccountNumber(entityProvider.resolveString(dto.getAccountNumber(), accountToModify.getAccountDetails().getAccountNumber()));
        accountToModify.getAccountDetails().setAccountHolder(entityProvider.resolveString(dto.getAccountHolder(), accountToModify.getAccountDetails().getAccountHolder()));
        accountToModify.getAccountDetails().setNomineeName(entityProvider.resolveString(dto.getNomineeName(), accountToModify.getAccountDetails().getNomineeName()));
        accountToModify.setCurrency(entityProvider.resolveCurrency(dto.getCurrency(), accountToModify.getCurrency()));
        accountToModify.getBalance().setAmount(entityProvider.resolveBalance(dto.getBalance(), accountToModify.getBalance().getAmount()));

        accountRepo.save(accountToModify);

        return accountMapper.toAccountResponseDto(accountToModify);

    }


    //    service level to delete Account
    @Transactional
    public DeleteAccountDto deleteAccount(User user, Long userId, Long subuserId, Long accountId) {

        Long mainUser = resolver.getTargetUserId(user, userId, subuserId);
        Account accountToDelete = accountRepo.findByUserIdAndId(mainUser, accountId).orElseThrow(() -> new RuntimeException("Account does not belong to user"));
        accountRepo.delete(accountToDelete);

        return accountMapper.toDeleteAccountDto(accountToDelete);

    }


    //    service level for getting any 1 Account details
    public AccountResponseDto getAccountDetials(User user, Long userId, Long subuserId, Long accountId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);
        Account account = accountRepo.findByUserIdAndId(targetUserId, accountId).orElseThrow(() -> new RuntimeException("Account does not belong to that User"));

        return accountMapper.toAccountResponseDto(account);
    }


    //    service level for getting All Account details
    @Transactional
    public AllAccountDetails getAllAccounts(User user, Long userId, Long subuserId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);

        List<AccountDetailsDto> allAccount = accountRepo.findByUserId(targetUserId);

        Map<String, List<AccountDetailsDto>> groupedAccounts = allAccount.stream()
                .collect(Collectors.groupingBy(a -> a.getPaymentMethod() != null ? a.getPaymentMethod().toUpperCase() : "UNKNOWN"));

        return AllAccountDetails.builder()
                .totalBalance(balanceRepo.getAllAccountBalance(targetUserId))
                .bankAccounts(groupedAccounts.getOrDefault("BANK", List.of()))
                .mobileBankingAccounts(groupedAccounts.getOrDefault("MOBILE_BANKING", List.of()))
                .cashWallets(groupedAccounts.getOrDefault("CASH", List.of()))
                .build();

    }


    //service for the getting all accounts list by MethodId
    public List<AccountDetailsDto> getAllAccountsOfMethodId(User user, Long userId, Long subuserId, Long paymentMethodId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);
        List<AccountDetailsDto> methodsAccounts = accountRepo.findByuserIdAndMethodId(targetUserId, paymentMethodId);

        return methodsAccounts;
    }


//    service for the softDeleted Account list

    public List<SoftDeletedAccountDto> getSoftDeletedAccounts(User user, Long userId, Long subuserId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);

        List<Object[]> softDeletedList = accountRepo.findSoftDeletedListByUserId(targetUserId);

        return softDeletedList.stream()
                .map(a -> SoftDeletedAccountDto.builder()
                        .accountId(((Number) a[0]).longValue())            //accountId -> object[0]
                        .deletedAt(((Timestamp) a[1]).toLocalDateTime())  //deleted_at -> object[1]
                        .accountNumber((String) a[2])                    //account_number -> object[2]
                        .paymentMethod((String) a[3])                   //paymentMethod -> object[3]
                        .providerName((String) a[4])                   //provider -> object[4]
                        //accountType -> object[5]
                        //balance -> object[6]
                        .build()).toList();
    }


    //service for reviveing the softdeleted account
    @Transactional
    public AccountResponseDto reviveAccountFromSoftDelete(User user, Long userId, Long subuserId, Long accountId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);
        Account deletedAccount = entityProvider.findByUserIdAndIdAndIsDeleted(targetUserId, accountId, true);
        accountRepo.reviveAccount(accountId);
        transactionRepo.reviveTransactionsByAccountId(accountId);
        balanceRepo.reviveBalanceByAccountId(accountId);
        accountDetailsRepo.reviveAccountDetialsByAccountId(accountId);

        return accountMapper.toAccountResponseDto(deletedAccount);
    }


    //service for deleteing account permanently
    @Transactional
    public DeleteAccountDto deleteAccountPermanently(User user, Long userId, Long subuserId, Long accountId) {
        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);

        Account permanentDeleted = entityProvider.findByUserIdAndIdAndIsDeleted(targetUserId, accountId, true);
        accountDetailsRepo.hardDeleteByAccountId(accountId);
        balanceRepo.hardDeleteByAccountId(accountId);
        transactionRepo.hardDeleteByAccountId(accountId);
        accountRepo.hardDeleteById(accountId);

        return accountMapper.toDeleteAccountDto(permanentDeleted);
    }


    //    service for getting account balance
    public AccountBalnaceDto getAccountBalance(User user, Long userId, Long subuserId, Long accountId) {

        Long targetUserId = resolver.getTargetUserId(user, userId, subuserId);
        verifier.checkAccountIdExistByUserId(targetUserId, accountId, false);
        Account account = accountRepo.findById(accountId).orElseThrow(() -> new RuntimeException("account not found"));

        return accountMapper.toAccountBalanceDto(account);
    }



    public TotalBalanceDto getTotalBalanceCurrencyWise(User user, Long userId, Long subuserId, Long currencyId) {

        Long targetUserId= resolver.getTargetUserId(user,userId,subuserId);

        Object[] result = accountRepo.getTotalBalanceByCurrency(targetUserId,currencyId);

        return TotalBalanceDto.builder()
                .totalBalance((BigDecimal) result[0])
                .totalAccounts(((Number) result[1]).longValue())
                .currency((String) result[2])
                .build();
    }
}

