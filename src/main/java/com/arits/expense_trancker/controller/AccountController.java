package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.MobileBanking;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.BankAccountRepo;
import com.arits.expense_trancker.repository.MobileBankingRepo;
import com.arits.expense_trancker.repository.UserRepo;
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
    private final MobileBankingRepo mobileBankingRepo;
    private final BankAccountRepo bankAccountRepo;


//Controller for the Adding an Account

    //For Adding Bank Account
    @PostMapping("/add-bank-account")
    @PreAuthorize("hasAuthority('Add Bank Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<AddBankAccountResponseDto>> addBankAccount(@AuthenticationPrincipal User user, @RequestBody AddBankAccountRequestDto addBankAccountRequestDTO) {


        AddBankAccountResponseDto newAccount = accountsService.addBankAccount(user, addBankAccountRequestDTO);

        ApiResponse<AddBankAccountResponseDto> response = ApiResponse.<AddBankAccountResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Bank Account added successfully")
                .timestamp(LocalDateTime.now())
                .result(newAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    //For Adding Mobile Banking Account
    @PostMapping("/add-mobile-Banking-account")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<ApiResponse<?>> addMobileBanking(@AuthenticationPrincipal User user, @RequestBody AddMobileBankingRequestDto addMobileBankingRequestDto) {

        AddMobileBankingResponseDto newAccount = accountsService.addMobileBankingAccount(user, addMobileBankingRequestDto);


        ApiResponse<?> response = ApiResponse.<AddMobileBankingResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Mobile Banking Account added Successfully")
                .timestamp(LocalDateTime.now())
                .result(newAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    //For creating cash Account
    @PostMapping("/create-cash-wallet-account")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<ApiResponse<?>> createCashWallet(@AuthenticationPrincipal User user, @RequestBody CreateCashAccountRequestDto dto) {

        CreateCashAccountResponseDto newWallet = accountsService.addCashAccount(user, dto);

        ApiResponse<?> response = ApiResponse.<CreateCashAccountResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Cash Wallet created Successfully")
                .timestamp(LocalDateTime.now())
                .result(newWallet)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    //Controller for Modifying Accounts Detail
    // for modifying bank details
    @PutMapping("modify-bank-details/{id}")
    @PreAuthorize("hasAuthority('Modify Bank Account Details') or hasRole('OWNER')")
    public ResponseEntity<?> modifyBankingDetails(@AuthenticationPrincipal User user, @PathVariable("id") long id, @RequestBody ModifyBankAccountDetailsRequestDto requestDTo) {

        ModifyBankAccountDetailsResponseDto modifyMobileBankAccount = accountsService.modifyBankAccountDetails(user, id, requestDTo);

        ApiResponse<?> response = ApiResponse.<ModifyBankAccountDetailsResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified bank details successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyMobileBankAccount)
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
    @DeleteMapping("/delete-mobile-bank-account/{id}")
    @PreAuthorize("hasAuthority('Delete Mobile Banking Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteMobileBankingAccount(@AuthenticationPrincipal User user, @PathVariable("id") long id) {

        DeleteAccountDto deletedBankAccount = accountsService.deleteMobileBankingAccount(user, id);

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
    @DeleteMapping("/delete-cash-wallet")
    @PreAuthorize("hasAuthority('Delete Mobile Banking Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteCashWallet(@AuthenticationPrincipal User user) {

        DeleteAccountDto deleteCashWallet = accountsService.deleteCashWallet(user);

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
    @GetMapping("/get-all-accounts-balance-group-wise")
    @PreAuthorize("hasAuthority('Get All Accounts Balance') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getAllAccountBalance(@AuthenticationPrincipal User user) {
        AllAccountBalanceDto allAccountBalance = accountsService.getAccountsBalance(user);

        ApiResponse<?> response = ApiResponse.<AllAccountBalanceDto>builder()
                .status(HttpStatus.OK.value())
                .message("fatched all account balance")
                .timestamp(LocalDateTime.now())
                .result(allAccountBalance)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //get bank account Details
    @GetMapping({"/owner/get-bank-account-Details/{id}",
            "/owner/get-bank-account-Details/{accountId}/{person}",
            "/admin/get-bank-account-Details/{accountId}/{person}"})

    @PreAuthorize("hasAnyAuthority('Get Bank Account Detail'," +
                                   "'Get Any Bank Account Detail')")
    public ResponseEntity<ApiResponse<?>> getBankAccountBalance(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "personId",required = false) Long personId,
            @PathVariable(value = "accountId", required = false) Long accountId) {


        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Bank Account Detail"));

        if (isHeSuperAdmin && personId != null && accountId != null) {

            passengerUser = userRepo.findById(personId).orElseThrow(() -> new RuntimeException("Invalid UserId"));
            accountId = bankAccountRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        } else if (!isHeSuperAdmin && accountId != null && personId != null) {

            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), personId).orElseThrow(() -> new RuntimeException("SubUser does not exist"));
            accountId = bankAccountRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        }else {

            accountId = bankAccountRepo.getUsersValidAccountId(user.getId(),accountId).orElseThrow(()->new RuntimeException("invalid accountId"));

        }
        List<BankAccountsDetailDto> accountBalance = accountsService.getBankAccountDetails(passengerUser, accountId);

        ApiResponse<?> apiResponse = ApiResponse.<List<BankAccountsDetailDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fetched all account balance")
                .timestamp(LocalDateTime.now())
                .result(accountBalance)
                .error(null)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    //get mobileBanking account Details
    @GetMapping({"/owner/get-mobile-banking-account-Details/{id}",
                "/owner/get-mobile-banking-account-Details/{accountId}/{personId}",
                "/admin/get-mobile-banking-account-Details/{accountId}/{personId}"})

    @PreAuthorize("hasAnyAuthority('Get Mobile Banking Account Detail'," +
                                  "'Get Any Mobile Banking Account Detail')")

    public ResponseEntity<ApiResponse<?>> getMobileBankingAccountDetails(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "personId", required = false) Long personId,
            @PathVariable(value = "accountId", required = false) Long accountId) {

        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any Mobile Banking Account Detail"));

        if (isHeSuperAdmin && personId != null && accountId != null) {

            passengerUser = userRepo.findById(personId).orElseThrow(() -> new RuntimeException("Invalid UserId"));
            accountId = mobileBankingRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        } else if (!isHeSuperAdmin && accountId != null && personId != null) {

            passengerUser = userRepo.findByParentIdAndUserId(user.getId(), personId).orElseThrow(() -> new RuntimeException("SubUser does not exist"));
            accountId = mobileBankingRepo.getUsersValidAccountId(personId, accountId).orElseThrow(() -> new RuntimeException("Invalid AccountId"));

        }else {

            accountId = mobileBankingRepo.getUsersValidAccountId(user.getId(),accountId).orElseThrow(()->new RuntimeException("invalid accountId"));

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
    @PreAuthorize("hasAnyAuthority('Get Cash Wallet Details','Get Any User Cash Wallet')")
    public ResponseEntity<ApiResponse<?>> getCashWalletDetails(@AuthenticationPrincipal User user, @PathVariable(value = "userId", required = false) Long userId) {

        User passengerUser = user;
        boolean isHeSuperAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("Get Any User Cash Wallet"));

        if (isHeSuperAdmin && userId != null) {
            passengerUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("user does not exist"));
        } else if (!isHeSuperAdmin && userId != null) {
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







