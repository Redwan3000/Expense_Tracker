package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.Account;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.*;
import com.arits.expense_trancker.service.AccountsService;
import jakarta.servlet.http.HttpServletRequest;
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
    @PreAuthorize("hasAnyAuthority(" +
            "'Add Account'," +
            "'Add Account For Any User'," +
            "'Add Account For Subowner'," +
            "'Add Account For Any Owners Subowner')")
    public ResponseEntity<ApiResponse<?>> addAccount(@AuthenticationPrincipal User user,
                                                     @Valid@RequestBody AddAccountRequestDto requestDto,
                                                      @PathVariable(name = "userId", required = false) Long userId,
                                                      @PathVariable(name = "subUserId", required = false) Long subuserId,
                                                     HttpServletRequest request) {


        if (userId != null && !userRepo.existsById(userId)) {
            throw new IllegalArgumentException("invalid userID");
        }

        if (subuserId != null && !userRepo.existsById(subuserId)) {
            throw new IllegalArgumentException("invalid userID");
        }


        if (request.getRequestURI().contains("/user/add-account")) {
            if (subuserId != null) {
                checkPermission(user, "Add Account For Subowner");
            } else {
                checkPermission(user, "Add Account");
            }

        } else if (request.getRequestURI().contains("/admin/user/add-account/")) {

            if (subuserId != null && userId!=null) {
                checkPermission(user, "Add Account For Any Owners Subowner");
            } else {
                checkPermission(user, "Add Account For Any User");
            }
        }

        AddAccountResponseDto newAccount = accountsService.addAccount(user, requestDto, userId, subuserId);
        ApiResponse<?> response = ApiResponse.<AddAccountResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Account Added Successfully")
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
            "/owner/subuser/modify-account-details/{paymentMethod}/{accountId}/{subUserId}",
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
                                                  @PathVariable(value = "subUserId", required = false) Long subuserId,
                                                  @RequestBody ModifyAccountDetailsRequestDto requestDto) {


        if (!accountRepo.existsByPaymentMethodAndId(paymentMethod, accountId)) {
            throw new IllegalArgumentException("invalid account details");
        }

        User passengerUser;
        Account validAccount;

        boolean canModifyOwnAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Modify Account Details"));
        boolean canModifySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Modify Account Details For Subuser"));
        boolean canModifyAnyUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Account Details For Any User"));
        boolean canModifyAnySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Account Details For Any Users Subuser"));

        if (canModifyOwnAccount && userId == null && subuserId == null) {

            validAccount = accountRepo.findByUserIdAndId(user.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifyAnyUserAccount && userId != null && subuserId == null) {
            validAccount = accountRepo.findByUserIdAndId(userId, accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifySubUserAccount && userId != null && subuserId == null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("invalid userID"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canModifyAnySubUserAccount && userId != null && subuserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subuserId).orElseThrow(() -> new RuntimeException("invalid subUser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else {
            throw new IllegalArgumentException("Invalid User");
        }

        if (paymentMethodRepo.findById(paymentMethod).stream().anyMatch(a -> a.getName().equals("CASH"))) {
            requestDto.setAccountHolder(null);
            requestDto.setAccountNumber(null);
            requestDto.setNomineeName(null);
        }


        AddAccountResponseDto modifyAccountDetails = accountsService.modifyAccountDetails(requestDto, validAccount.getId());

        ApiResponse<?> response = ApiResponse.<AddAccountResponseDto>builder()
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

    @PreAuthorize("hasAnyAuthority(" +
            "'Delete Account'," +
            "'Delete Subuser Account'," +
            "'Delete Any User Account'," +
            "'Delete Any Users Subuser Account')")

    public ResponseEntity<ApiResponse<?>> deleteAccount(@AuthenticationPrincipal User user,
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

        boolean canDeleteOwnAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Account"));
        boolean canDeleteSubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Subuser Account"));
        boolean canDeleteAnyUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Any User Account"));
        boolean canDeleteAnySubUserAccount = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Delete Any Users Subuser Account"));


        if (canDeleteOwnAccount && userId == null && subuserId == null) {

            validAccount = accountRepo.findByUserIdAndId(user.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canDeleteAnyUserAccount && userId != null && subuserId == null) {
            validAccount = accountRepo.findByUserIdAndId(userId, accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canDeleteSubUserAccount && userId != null && subuserId == null) {
            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), userId).orElseThrow(() -> new RuntimeException("Invalid Subuser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else if (canDeleteAnySubUserAccount && userId != null && subuserId != null) {
            passengerUser = userRepo.findByParentIdAndUserId(userId, subuserId).orElseThrow(() -> new RuntimeException("invalid subUser"));
            validAccount = accountRepo.findByUserIdAndId(passengerUser.getId(), accountId).orElseThrow(() -> new RuntimeException("Account does not belongs to this user"));
        } else {
            throw new IllegalArgumentException("Invalid Account");
        }


        DeleteAccountDto deletedBankAccount = accountsService.deleteAccount(validAccount);

        ApiResponse<?> response = ApiResponse.<DeleteAccountDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Account Deleted Successfully Along With The Transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

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







