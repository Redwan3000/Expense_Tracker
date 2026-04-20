package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.service.AccountsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {


    private final AccountsService accountsService;
    private final Verifier verifier;


//Controller for the Adding an Account
    //owner can add account for them also for their subowner
    //admin can also add account for any user

    @PostMapping({
            "/user/add-account",
            "/user/add-account/{subUserId}",
            "/admin/user/add-account/{userId}",
            "/admin/user/add-account/{userId}/{subUserId}"})

    public ResponseEntity<ApiResponse<?>> addAccount(@AuthenticationPrincipal User user,
                                                     @Valid @RequestBody AddAccountRequestDto requestDto,
                                                     @PathVariable(name = "userId", required = false) Long userId,
                                                     @PathVariable(name = "subUserId", required = false) Long subuserId) {


        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);

        AccountResponseDto newAccount = accountsService.addAccount(user, requestDto, userId, subuserId);
        ApiResponse<AccountResponseDto> response = ApiResponse.<AccountResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Account Added Successfully")
                .timestamp(LocalDateTime.now())
                .result(newAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    //Controller for Modifying Accounts Detail
    // for modifying account details
    @PutMapping({
            "/user/modify-account-details/{methodId}/{accountId}",
            "/owner/subuser/modify-account-details/{methodId}/{accountId}/{subUserId}",
            "/admin/user/modify-account-details/{methodId}/{accountId}/{userId}",
            "/admin/subowner/modify-account-details/{methodId}/{accountId}/{subUserId}/{userId}"})
    public ResponseEntity<?> modifyAccountDetails(@AuthenticationPrincipal User user,
                                                  @PathVariable(value = "methodId") Long methodId,
                                                  @PathVariable(value = "accountId") Long accountId,
                                                  @PathVariable(value = "userId", required = false) Long userId,
                                                  @PathVariable(value = "subUserId", required = false) Long subuserId,
                                                  @Valid @RequestBody ModifyAccountDetailsRequestDto requestDto) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
//        verifier.checkAccountExistence(accountId);
//        verifier.checkPaymentMethodExistence(methodId);
        verifier.checkAccountIdExistByPaymentMethod(methodId, accountId);


        AccountResponseDto modifyAccountDetails = accountsService.modifyAccountDetails(user, requestDto, userId, subuserId, accountId);

        ApiResponse<AccountResponseDto> response = ApiResponse.<AccountResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Modified Account Details Successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyAccountDetails)
                .error(null)
                .build();

        return ResponseEntity.ok(response);

    }


//Controller for Deleteing Accounts

    @DeleteMapping({
            "/user/delete-account/{paymentMethod}/{accountId}",
            "/owner/subuser/delete-account/{paymentMethod}/{accountId}/{userId}",
            "/admin/user/delete-account/{paymentMethod}/{accountId}/{userId}",
            "/admin/subuser/delete-account/{paymentMethod}/{accountId}/{userId}/{subuserId}"})

    public ResponseEntity<ApiResponse<?>> deleteAccount(@AuthenticationPrincipal User user,
                                                        @PathVariable(value = "paymentMethod") Long paymentMethod,
                                                        @PathVariable(value = "accountId") Long accountId,
                                                        @PathVariable(value = "userId", required = false) Long userId,
                                                        @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
        verifier.checkAccountIdExistByPaymentMethod(paymentMethod, accountId);


        DeleteAccountDto deletedBankAccount = accountsService.deleteAccount(user, userId, subuserId, accountId);

        ApiResponse<DeleteAccountDto> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.OK.value())
                .message("Account Deleted Successfully Along With The Transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


//Controller For Getting any Account Details

    @GetMapping({
            "/user/get-account-detials/{paymentMethod}/{accountId}",
            "/owner/subuser/get-account-detials/{paymentMethod}/{accountId}/{userId}",
            "/admin/user/get-account-detials/{paymentMethod}/{accountId}/{userId}",
            "/admin/subuser/get-account-detials/{paymentMethod}/{accountId}/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> getAccountDetails(@AuthenticationPrincipal User user,
                                                            @PathVariable(value = "paymentMethod") Long paymentMethod,
                                                            @PathVariable(value = "accountId") Long accountId,
                                                            @PathVariable(value = "userId", required = false) Long userId,
                                                            @PathVariable(value = "subuserId", required = false) Long subuserId) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
        verifier.checkAccountIdExistByPaymentMethod(paymentMethod, accountId);

        AccountResponseDto allAccountBalance = accountsService.getAccountDetials(user, userId, subuserId, accountId);

        ApiResponse<?> response = ApiResponse.<AccountResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched Account Details")
                .timestamp(LocalDateTime.now())
                .result(allAccountBalance)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //Controller For Getting all Account Details
    @GetMapping({
            "/user/get-all-account-details",
            "/owner/subuser/get-all-account-details/{subuserId}",
            "/admin/user/get-all-account-details/{userId}",
            "/admin/subuser/get-all-account-details/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> getAllAccountDetails(@AuthenticationPrincipal User user,
                                                               @PathVariable(value = "userId", required = false) Long userId,
                                                               @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);


        AllAccountDetails allAccountDetails = accountsService.getAllAccounts(user, userId, subuserId);

        ApiResponse<AllAccountDetails> response = ApiResponse.<AllAccountDetails>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched All Account Details Successfully")
                .timestamp(LocalDateTime.now())
                .result(allAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //Controller For Getting all Account Details method Wise
    @GetMapping({
            "/user/get-any-method-all-account-details/{paymentMethodId}",
            "/owner/subuser/get-any-method-all-account-details/{paymentMethodId}/{subuserId}",
            "/admin/user/get-any-method-all-account-details/{paymentMethodId}/{userId}",
            "/admin/subuser/get-all-any-method-account-details/{paymentMethodId}/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> getAnyMethodAllAccountDetails(@AuthenticationPrincipal User user,
                                                                        @PathVariable(value = "paymentMethodId") Long paymentMethodId,
                                                                        @PathVariable(value = "userId", required = false) Long userId,
                                                                        @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
        verifier.checkPaymentMethodExistence(paymentMethodId);

        List<AccountDetailsDto> allAccountDetails = accountsService.getAllAccountsOfMethodId(user, userId, subuserId, paymentMethodId);

        ApiResponse<List<AccountDetailsDto>> response = ApiResponse.<List<AccountDetailsDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched All Account Details By Methods Successfully")
                .timestamp(LocalDateTime.now())
                .result(allAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //Controller For Getting all softDeleted Account Details
    @GetMapping({
            "/user/get-soft-deleted-account-list",
            "/owner/subuser/get-soft-deleted-account-list/{subuserId}",
            "/admin/user/get-soft-deleted-account-list/{userId}",
            "/admin/subuser/get-soft-deleted-account-list/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> getAllDeletedAccounts(@AuthenticationPrincipal User user,
                                                                @PathVariable(value = "userId", required = false) Long userId,
                                                                @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);

        List<SoftDeletedAccountDto> softDeletedAccountDetails = accountsService.getSoftDeletedAccounts(user, userId, subuserId);

        ApiResponse<List<SoftDeletedAccountDto>> response = ApiResponse.<List<SoftDeletedAccountDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched All Account Details By Methods Successfully")
                .timestamp(LocalDateTime.now())
                .result(softDeletedAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //Controller For restoring accounts from softDeleted
    @PutMapping({
            "/user/restore-account/{methodId}/{accountId}",
            "/owner/subuser/restore-account/{methodId}/{accountId}/{subuserId}",
            "/admin/user/restore-account/{methodId}/{accountId}/{userId}",
            "/admin/subuser/restore-account/{methodId}/{accountId}/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> restoreAccount(@AuthenticationPrincipal User user,
                                                         @PathVariable(value = "userId", required = false) Long userId,
                                                         @PathVariable(value = "subuserId", required = false) Long subuserId,
                                                         @PathVariable(value = "accountId") Long accountId,
                                                         @PathVariable(value = "methodId") Long methodId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
//        verifier.checkSoftDeletedPaymentMethodExistence(methodId);
//        verifier.checkSoftDeletedAccountExistence(accountId);
        verifier.checkSoftDeletedAccountIdExistByPaymentMethod(methodId,accountId);


        AccountResponseDto softDeletedAccountDetails = accountsService.reviveAccountFromSoftDelete(user, userId, subuserId,accountId);

        ApiResponse<AccountResponseDto> response = ApiResponse.<AccountResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("recovered Account Successfully")
                .timestamp(LocalDateTime.now())
                .result(softDeletedAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }

    //Controller For permanently deleting the accounts from the softdelete
    @PutMapping({
            "/user/delete-account-permanently/{methodId}/{accountId}",
            "/owner/subuser/delete-account-permanently/{methodId}/{accountId}/{subuserId}",
            "/admin/user/delete-account-permanently/{methodId}/{accountId}/{userId}",
            "/admin/subuser/delete-account-permanently/{methodId}/{accountId}/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> deleteAccountPermanently(@AuthenticationPrincipal User user,
                                                         @PathVariable(value = "userId", required = false) Long userId,
                                                         @PathVariable(value = "subuserId", required = false) Long subuserId,
                                                         @PathVariable(value = "accountId") Long accountId,
                                                         @PathVariable(value = "methodId") Long methodId
    ) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
//      verifier.checkSoftDeletedPaymentMethodExistence(methodId);
//      verifier.checkSoftDeletedAccountExistence(accountId);
        verifier.checkSoftDeletedAccountIdExistByPaymentMethod(methodId, accountId);


        DeleteAccountDto softDeletedAccountDetails = accountsService.deleteAccountPermanently(user, userId, subuserId, accountId);

        ApiResponse<DeleteAccountDto> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.OK.value())
                .message("recovered Account Successfully")
                .timestamp(LocalDateTime.now())
                .result(softDeletedAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);

    }

        //Controller For getting account balance
        @GetMapping({
                "/user/get-account-balance/{methodId}/{accountId}",
                "/owner/subuser/get-account-balance/{methodId}/{accountId}/{subuserId}",
                "/admin/user/get-account-balance/{methodId}/{accountId}/{userId}",
                "/admin/subuser/get-account-balance/{methodId}/{accountId}/{userId}/{subuserId}",
        })
        public ResponseEntity<ApiResponse<?>> getAccountBalance (@AuthenticationPrincipal User user,
                                                                 @PathVariable(value = "userId", required = false) Long userId,
                                                                 @PathVariable(value = "subuserId", required = false) Long subuserId,
                                                                 @PathVariable(value = "accountId") Long accountId,
                                                                 @PathVariable(value = "methodId") Long methodId
    ){

            verifier.checkUserExistence(userId);
            verifier.checkUserExistence(subuserId);
            verifier.checkAccountIdExistByPaymentMethod(methodId, accountId);


            AccountBalnaceDto softDeletedAccountDetails = accountsService.getAccountBalance(user, userId, subuserId, accountId);

            ApiResponse<AccountBalnaceDto> response = ApiResponse.<AccountBalnaceDto>builder()
                    .status(HttpStatus.OK.value())
                    .message("recovered Account Successfully")
                    .timestamp(LocalDateTime.now())
                    .result(softDeletedAccountDetails)
                    .error(null)
                    .build();
            return ResponseEntity.ok(response);


        }


    //Controller For getting account total balance
    @GetMapping({
            "/user/get-total-balance/{currencyId}",
            "/owner/subuser/get-total-balance/{currencyId}/{subuserId}",
            "/admin/user/get-total-balance/{currencyId}/{userId}",
            "/admin/subuser/get-total-balance/{currencyId}/{userId}/{subuserId}",
    })
    public ResponseEntity<ApiResponse<?>> getTotalBalance (@AuthenticationPrincipal User user,
                                                           @PathVariable(value = "userId", required = false) Long userId,
                                                           @PathVariable(value = "subuserId", required = false) Long subuserId,
                                                           @PathVariable(value = "currencyId") Long currencyId
    ){

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
        verifier.checkCurrencyExistance(currencyId);


        TotalBalanceDto softDeletedAccountDetails = accountsService.getTotalBalanceCurrencyWise(user, userId, subuserId, currencyId);

        ApiResponse<TotalBalanceDto> response = ApiResponse.<TotalBalanceDto>builder()
                .status(HttpStatus.OK.value())
                .message("recovered Account Successfully")
                .timestamp(LocalDateTime.now())
                .result(softDeletedAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);


    }

    }








