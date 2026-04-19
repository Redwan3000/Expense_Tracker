package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.Account;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.repository.*;
import com.arits.expense_trancker.service.AccountsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AccountController {


    private final AccountsService accountsService;
    private final UserRepo userRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final CurrencyRepo currencyRepo;
    private final AccountRepo accountRepo;
    private final Verifier verifier;

    public void checkPermission(User user, String permission) {

        boolean haspermission = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(permission));
        if (!haspermission) {
            throw new RuntimeException("Do Not Have Permission");
        }

    }

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
        verifier.checkAccountExistence(accountId);
        verifier.checkPaymentMethodExistence(methodId);
        verifier.checkAccountIdExistByPaymentMethod(methodId, accountId);


        AccountResponseDto modifyAccountDetails = accountsService.modifyAccountDetails(user, requestDto, accountId, userId, subuserId);

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
        verifier.checkAccountExistence(accountId);
        verifier.checkPaymentMethodExistence(paymentMethod);
        verifier.checkAccountIdExistByPaymentMethod(paymentMethod, accountId);


        DeleteAccountDto deletedBankAccount = accountsService.deleteAccount(user.getId(),accountId,userId,subuserId);

        ApiResponse<DeleteAccountDto> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.OK.value())
                .message("Account Deleted Successfully Along With The Transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


//Controller For Getting Account Details

    //Detailed Account Balance
    @GetMapping({
            "/user/get-account-detials/{paymentMethod}/{accountId}",
            "/owner/subuser/get-account-detials/{paymentMethod}/{accountId}/{userId}",
            "/admin/user/get-account-detials/{paymentMethod}/{accountId}/{userId}",
            "/admin/subuser/get-account-detials/{paymentMethod}/{accountId}/{userId}/{subuserId}",
    })
    @PreAuthorize("hasAnyAuthority(" +
            "'Get Own Account Details'," +
            "'Get Subuser Account Details'," +
            "'Get Any User Account Details'," +
            "'Get Any Users Subuser Account Details')")

    public ResponseEntity<ApiResponse<?>> getAccountDetails(@AuthenticationPrincipal User user,
                                                            @PathVariable(value = "paymentMethod", required = false) Long paymentMethod,
                                                            @PathVariable(value = "accountId", required = false) Long accountId,
                                                            @PathVariable(value = "userId", required = false) Long userId,
                                                            @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        if (!accountRepo.existsByPaymentMethodAndId(paymentMethod, accountId)) {
            throw new IllegalArgumentException("invalid account details");
        }

        User passengerUser;
        Account validAccount;

        boolean canGetOwnAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Own Account Details"));
        boolean canGetSubuserAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Subuser Account Details"));
        boolean canGetAnyAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any User Account Details"));
        boolean canGetAnySubuserAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Users Subuser Account Details"));


        if (canGetOwnAccountDetails && userId == null && subuserId == null) {
            validAccount = accountRepo.findByUserIdAndId(user.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canGetAnyAccountDetails && userId != null && subuserId == null) {
            validAccount = accountRepo.findByUserIdAndId(userId, accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canGetSubuserAccountDetails && userId != null && subuserId == null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("Invalid Subuser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canGetAnySubuserAccountDetails && userId != null && subuserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subuserId).orElseThrow(() -> new RuntimeException("invalid subUser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else {
            throw new IllegalArgumentException("Invalid Account");
        }


        AccountDetailsResponseDto allAccountBalance = accountsService.getAccountDetials(validAccount);

        ApiResponse<?> response = ApiResponse.<AccountDetailsResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched Account Details")
                .timestamp(LocalDateTime.now())
                .result(allAccountBalance)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping({
            "/user/get-all-account-details",
            "/owner/subuser/get-all-account-details/{userId}",
            "/admin/user/get-all-account-details/{userId}",
            "/admin/subuser/get-all-account-details/{userId}/{subuserId}",
    })
    @PreAuthorize("hasAnyAuthority(" +
            "'Get Own All Account Details'," +
            "'Get Subuser All Account Details'," +
            "'Get Any User All Account Details'," +
            "'Get Any Users Subuser All Account Details')")

    public ResponseEntity<ApiResponse<?>> getAllAccountDetails(@AuthenticationPrincipal User user,

                                                               @PathVariable(value = "userId", required = false) Long userId,
                                                               @PathVariable(value = "subuserId", required = false) Long subuserId
    ) {

        User passengerUser;
        Account validAccount;

        boolean canGetOwnAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Own All Account Details"));
        boolean canGetSubuserAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Subuser All Account Details"));
        boolean canGetAnyAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any User All Account Details"));
        boolean canGetAnySubuserAccountDetails = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Users Subuser All Account Details"));


        if (canGetOwnAccountDetails && userId == null && subuserId == null) {
            passengerUser = user;
        } else if (canGetAnyAccountDetails && userId != null && subuserId == null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("user does not exist"));
        } else if (canGetSubuserAccountDetails && userId != null && subuserId == null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("Subuser does not exist"));
        } else if (canGetAnySubuserAccountDetails && userId != null && subuserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subuserId).orElseThrow(() -> new RuntimeException("Subuser does not exist"));
        } else {
            throw new IllegalArgumentException("invelid userIds");
        }


        AllAccountDetails allAccountDetails = accountsService.getAllAccounts(passengerUser.getId());

        ApiResponse<?> response = ApiResponse.<AllAccountDetails>builder()
                .status(HttpStatus.OK.value())
                .message("Fatched All Account Details At a Time")
                .timestamp(LocalDateTime.now())
                .result(allAccountDetails)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


}







