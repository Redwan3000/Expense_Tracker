package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.Account;
import com.arits.expense_trancker.entity.Balance;
import com.arits.expense_trancker.entity.Currency;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.*;
import com.arits.expense_trancker.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {


    private final AccountsService accountsService;
    private final UserRepo userRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final CurrencyRepo currencyRepo;
    private final AccountRepo accountRepo;


//Controller for the Adding an Account
    //owner can add account for them also for their subowner
    //admin can also add account for any user

    @PostMapping({
            "/user/add-account",
            "/owner/user/add-account/{userId}",
            "/admin/user/add-account/{userId}",
            "/admin/subowner/add-account/{userId}/{subUserId}"})
    @PreAuthorize("hasAnyAuthority(" +
            "'Add Account'," +
            "'Add Account For Any User'," +
            "'Add Account For Subowner'," +
            "'Add Account For Any Owners Subowner')")
    public ResponseEntity<ApiResponse<?>> addAccount(@AuthenticationPrincipal User user,
                                                     @RequestBody AddAccountRequestDto requestDto,
                                                     @PathVariable(name = "userId", required = false) Long userId,
                                                     @PathVariable(name = "subUserId", required = false) Long subUserId) {


        User passengerUser;

        boolean canAddAnyUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Add Account For Any User"));
        boolean canAddSubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Add Account For Subowner"));
        boolean canAddAnySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Add Account For Any Owners Subowner"));
        boolean canAddOwnAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Add Account"));

        if (canAddOwnAccount && userId == null && subUserId == null) {
            passengerUser = user;
        } else if (canAddAnyUserAccount && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("invalid userId"));
        } else if (canAddSubUserAccount && userId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userID"));
        } else if (canAddAnySubUserAccount && userId != null && subUserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subUserId).orElseThrow(() -> new RuntimeException("invalid subUser"));
        } else {
            throw new IllegalArgumentException("Invalid User");
        }

        AddAccountResponseDto newAccount = accountsService.addAccount(passengerUser, requestDto);

        ApiResponse<?> response = ApiResponse.<AddAccountResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Bank Account added successfully")
                .timestamp(LocalDateTime.now())
                .result(newAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    //Controller for Modifying Accounts Detail
    // for modifying bank details
    @PutMapping({
            "/user/modify-account-details/{paymentMethod}/{accountId}",
            "/owner/user/modify-account-details/{paymentMethod}/{accountId}/{subUserId}",
            "/admin/user/modify-account-details/{paymentMethod}/{accountId}/{userId}",
            "/admin/subowner/modify-account-details/{paymentMethod}/{accountId}/{subUserId}/{userId}"})
    @PreAuthorize("hasAnyAuthority(" +
            "'Modify Account Details'," +
            "'Modify Account Details For Subuser'," +
            "'Modify Account Details For Any User'," +
            "'Modify Account Details For Any Users Subuser')")
    public ResponseEntity<?> modifyBankingDetails(@AuthenticationPrincipal User user,
                                                  @PathVariable(value = "paymentMethod", required = false) Long paymentMethod,
                                                  @PathVariable(value = "accountId", required = false) Long accountId,
                                                  @PathVariable(value = "userId", required = false) Long userId,
                                                  @PathVariable(value = "subUserId", required = false) Long subUserId,
                                                  @RequestBody ModifyAccountDetailsRequestDto requestDTo) {


        if (!accountRepo.existsByPaymentMethodAndId(paymentMethod, accountId)) {
            throw new IllegalArgumentException("invalid account details");
        }
        User passengerUser;
        Account validAccount;
        boolean canModifyOwnAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Modify Account Details"));
        boolean canModifySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Modify Account Details For Subuser"));
        boolean canModifyAnyUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Account Details For Any User"));
        boolean canModifyAnySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Account Details For Any Users Subuser"));

        if (canModifyOwnAccount && userId == null && subUserId == null) {
            passengerUser = user;
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifyAnyUserAccount && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("invalid userId"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifySubUserAccount && userId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userID"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifyAnySubUserAccount && userId != null && subUserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subUserId).orElseThrow(() -> new RuntimeException("invalid subUser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else {
            throw new IllegalArgumentException("Invalid User");
        }

        AddAccountResponseDto modifyAccountDetails = accountsService.modifyAccountDetails(passengerUser.getId(), requestDTo, paymentMethod, validAccount.getId());

        ApiResponse<?> response = ApiResponse.<AddAccountResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified bank details successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyAccountDetails)
                .error(null)
                .build();

        return ResponseEntity.ok(response);

    }

    // for modifying mobile banking details
    @PutMapping("modify-mobile-bank-details/{id}")
    @PreAuthorize("hasAuthority('Modify Bank Account Details') or hasRole('OWNER')")
    public ResponseEntity<?> modifyMobileBankingDetails(@AuthenticationPrincipal User user, @PathVariable("id") long id, @RequestBody ModifyBankAccountDetailsRequestsDTo modifyMobileBankingDetailsRequestDto) {

        ModifyMobileBankingDetailsResponseDto modifyMobileBankAccount = accountsService.modifyMobileBankingAccountDetails(user, id, modifyMobileBankingDetailsRequestDto);

        ApiResponse<?> response = ApiResponse.<ModifyMobileBankingDetailsResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified bank details successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyMobileBankAccount)
                .error(null)
                .build();

        return ResponseEntity.ok(response);

    }

    // for modifying cash wallet details
    @PutMapping("/modify-cash-wallet-details")
    public ResponseEntity<?> modifyCashWalletDetails(@AuthenticationPrincipal User user, @RequestBody ModifyCashWalletDetailsDto modifyCashWalletDetailsDto) {

        CreateCashAccountResponseDto modifyCashWallet = accountsService.modifyCashWallet(user, modifyCashWalletDetailsDto);

        ApiResponse<?> response = ApiResponse.<CreateCashAccountResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified bank details successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyCashWallet)
                .error(null)
                .build();

        return ResponseEntity.ok(response);

    }


//Controller for Deleteing Accounts

    //for deleting bank account
    @DeleteMapping("/delete-bank-account/{id}")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteBankAccount(@AuthenticationPrincipal User user, @PathVariable("id") long id) {

        DeleteAccountDto deletedBankAccount = accountsService.deleteBankAccount(user, id);

        ApiResponse<?> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }


    //For Deleting Mobile Banking Account
    @DeleteMapping({
            "/owner/delete-mobile-bank-account/{accountId}",
            "/owner/delete-subuser-mobile-bank-account/{accountId}/{userId}",
            "/admin/delete-mobile-bank-account/{accountId}/{userId}"})
    @PreAuthorize("hasAnyAuthority(" +
            "'Delete Mobile Banking Account'," +
            "'Delete Subuser Mobile Banking Account'," +
            "'Delete Any Mobile Banking Account')")
    public ResponseEntity<ApiResponse<?>> deleteMobileBankingAccount(@AuthenticationPrincipal User user,
                                                                     @PathVariable("accountId") long accountId,
                                                                     @PathVariable(value = "userId", required = false) Long userId) {


        User passengerUser = user;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Any Mobile Banking Account"));
        boolean canOwnerDelete = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Subuser Mobile Banking Account"));

        if (isHeAdmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("invalid userId"));

        } else if (!isHeAdmin && userId != null && canOwnerDelete) {

            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userId"));

        }

        Long validAccountId = mobileBankingRepo.getUsersValidAccountId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("invalid accountId"));


        DeleteAccountDto deletedBankAccount = accountsService.deleteMobileBankingAccount(passengerUser.getId(), validAccountId);

        ApiResponse<?> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }


    //For Deleting Cash Wallet Account
    @DeleteMapping({"/owner/delete-cash-wallet",
            "/owner/delete-subuser-cash-wallet/{userId}",
            "/admin/delete-cash-wallet/{userId}"})
    @PreAuthorize("hasAnyAuthority(" +
            "'Delete Cash Wallet Account'," +
            "'Delete Any Cash Wallet Account'," +
            "'Delete SubUser Cash Wallet Account')")

    public ResponseEntity<ApiResponse<?>> deleteCashWallet(@AuthenticationPrincipal User user,
                                                           @PathVariable(value = "userId", required = false) Long userId) {

        User passengerUser = user;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Any Cash Wallet Account"));
        boolean canUserDelete = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete SubUser Cash Wallet Account"));

        if (isHeAdmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Invalid UserId"));
        } else if (!isHeAdmin && userId != null && canUserDelete) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userId"));
        }


        DeleteAccountDto deleteCashWallet = accountsService.deleteCashWallet(passengerUser.getId());

        ApiResponse<?> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deleteCashWallet)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }

    //For Deleting Cash Wallet Account
    @DeleteMapping({
            "/owner/delete-account/{paymentId}/{accountId}",
            "/owner/delete-account/{paymentId}/{accountId}/{userId}",
            "/admin/delete-account/{paymentId}/{accountId}/{userId}00"})
    @PreAuthorize("hasAnyAuthority(" +
            "'Delete Account'," +
            "'Delete Any User Account'," +
            "'Delete SubUser Account')")

    public ResponseEntity<ApiResponse<?>> deleteAccount(@AuthenticationPrincipal User user,
                                                        @PathVariable(value = "paymentId", required = false) Long paymentId,
                                                        @PathVariable(value = "accountId", required = false) Long accountId,
                                                        @PathVariable(value = "userId", required = false) Long userId) {


        User passengerUser = user;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Any User Account"));
        boolean canDeleteSubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete SubUser Account Account"));

        if (isHeAdmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Invalid UserId"));
        } else if (!isHeAdmin && userId != null && canDeleteSubUserAccount) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userId"));
        }


        DeleteAccountDto deleteCashWallet = accountsService.deleteCashWallet(passengerUser.getId());

        ApiResponse<?> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deleteCashWallet)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }


//Controller For Getting Account Balance and Details

    //Detailed Account Balance
    @GetMapping({"/owner/get-all-accounts-balance-in-total",
            "/owner/get-all-accounts-balance-in-total/{userId}",
            "/admin/get-all-accounts-balance-in-total/{userId}",
    })
    @PreAuthorize("hasAnyAuthority('Get All Accounts Balance'," +
            "'Get SubUser All Account Balance'," +
            "'Get Any User All Account Balance') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<ApiResponse<?>> getAllAccountBalance(@AuthenticationPrincipal User user, @PathVariable(value = "userId", required = false) Long userId) {
        User passengerUser = user;
        boolean isHeADmin = user.getAuthorities().stream().anyMatch(p -> p.getAuthority().equals("Get Any User All Account Balance"));
        boolean canOwnerOversee = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get SubUser All Account Balance"));

        if (isHeADmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Invalid userId"));
        } else if (!isHeADmin && canOwnerOversee && userId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("Invalid userId / subUser does not exist"));
        }

        AllAccountBalanceDto allAccountBalance = accountsService.getAccountsBalance(passengerUser);

        ApiResponse<?> response = ApiResponse.<AllAccountBalanceDto>builder()
                .status(HttpStatus.OK.value())
                .message("fatched all account balance")
                .timestamp(LocalDateTime.now())
                .result(allAccountBalance)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //for getting bank account details of subOnwers and owner's own and also for admin to get any usres bank account Details
    @GetMapping({"/owner/get-bank-account-details/{id}",
            "/owner/get-bank-account-details/{accountId}/{person}",
            "/admin/get-bank-account-details/{accountId}/{person}"})

    @PreAuthorize("hasAnyAuthority('Get Bank Account Detail'," +
            "'Get Any Bank Account Detail'," +
            "'Get SubUser Bank Account Detail')")
    public ResponseEntity<ApiResponse<?>> getBankAccountBalance(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "personId", required = false) Long personId,
            @PathVariable(value = "accountId", required = false) Long accountId) {


        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Bank Account Detail"));
        boolean canOwnerOversee = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get SubUser Bank Account Detail"));

        if (isHeSuperAdmin && personId != null && accountId != null) {

            passengerUser = userRepo.findById(personId).orElseThrow(() -> new RuntimeException("Invalid UserId"));

        } else if (!isHeSuperAdmin && canOwnerOversee && accountId != null && personId != null) {

            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), personId).orElseThrow(() -> new RuntimeException("SubUser does not exist"));

        }

        Long validAccountId = bankAccountRepo.getUsersValidAccountId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        List<BankAccountsDetailDto> accountBalance = accountsService.getBankAccountDetails(passengerUser, validAccountId);

        ApiResponse<?> apiResponse = ApiResponse.<List<BankAccountsDetailDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fetched all account balance")
                .timestamp(LocalDateTime.now())
                .result(accountBalance)
                .error(null)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    //for getting mobile banking account details of subOnwers and owner's own and also for admin to get any usres mobile banking account Details
    @GetMapping({"/owner/get-mobile-banking-account-details/{id}",
            "/owner/get-mobile-banking-account-details/{accountId}/{personId}",
            "/admin/get-mobile-banking-account-details/{accountId}/{personId}"})

    @PreAuthorize("hasAnyAuthority('Get Mobile Banking Account Detail'," +
            "'Get Any Mobile Banking Account Detail'," +
            "'Get SubUser Mobile Banking Account Detail')")

    public ResponseEntity<ApiResponse<?>> getMobileBankingAccountDetails(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "personId", required = false) Long personId,
            @PathVariable(value = "accountId", required = false) Long accountId) {

        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Mobile Banking Account Detail"));
        boolean canOwnerOversee = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get SubUser Mobile Banking Account Detail"));

        if (isHeSuperAdmin && personId != null && accountId != null) {

            passengerUser = userRepo.findById(personId).orElseThrow(() -> new RuntimeException("Invalid UserId"));
            accountId = mobileBankingRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        } else if (!isHeSuperAdmin && canOwnerOversee && accountId != null && personId != null) {

            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), personId).orElseThrow(() -> new RuntimeException("SubUser does not exist"));
            accountId = mobileBankingRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        } else {

            accountId = mobileBankingRepo.getUsersValidAccountId(user.getId(), accountId).orElseThrow(() -> new RuntimeException("invalid accountId"));

        }

        List<MobileAccountsDetailsDto> accountDetails = accountsService.getMobileBankingAccountDetails(passengerUser.getId(), accountId);

        ApiResponse<?> apiResponse = ApiResponse.<List<MobileAccountsDetailsDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fetched account balance")
                .timestamp(LocalDateTime.now())
                .result(accountDetails)
                .error(null)
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    //for getting cash wallet details of subOnwers and owner's own and also for admin to get any usres cash wallet Details
    @GetMapping({"/owner/get-cash-wallet-details",
            "/owner/get-cash-wallet-details/{userId}",
            "/admin/get-cash-wallet-details/{userId}"})
    @PreAuthorize("hasAnyAuthority('Get Cash Wallet Details'," +
            "'Get Any User Cash Wallet details'," +
            "'Get SubUser Cash Wallet Details')")
    public ResponseEntity<ApiResponse<?>> getCashWalletDetails(@AuthenticationPrincipal User user, @PathVariable(value = "userId", required = false) Long userId) {

        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any User Cash Wallet"));
        boolean canOwnerOversee = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get SubUser Cash Wallet Details"));
        if (isHeSuperAdmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("user does not exist"));
        } else if (!isHeSuperAdmin && canOwnerOversee && userId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("subUser does not exist"));
        }


        CashWalletDetailsDto accountDetails = accountsService.getCashWalletAccountDetails(passengerUser);

        ApiResponse<?> apiResponse = ApiResponse.<CashWalletDetailsDto>builder()
                .status(HttpStatus.OK.value())
                .message("Fetched all account balance")
                .timestamp(LocalDateTime.now())
                .result(accountDetails)
                .error(null)
                .build();

        return ResponseEntity.ok(apiResponse);

    }


}







