package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.service.AccountsService;
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

    @PostMapping("/add-mobile-Banking-account")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasRole('OWNER')")
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


    @PostMapping("/create-cash-wallet-account")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasRole('OWNER')")
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


    @DeleteMapping("/delete-bank-account/{id}")
    @PreAuthorize("hasAuthority('Add Mobile Banking Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteBankAccount(@AuthenticationPrincipal User user, @PathVariable("id") long id) {

        AddBankAccountResponseDto deletedBankAccount = accountsService.deleteBankAccount(user, id);

        ApiResponse<?> response = ApiResponse.<AddBankAccountResponseDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/delete-mobile-bank-account/{id}")
    @PreAuthorize("hasAuthority('Delete Mobile Banking Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteMobileBankingAccount(@AuthenticationPrincipal User user, @PathVariable("id") long id) {

        AddMobileBankingResponseDto deletedBankAccount = accountsService.deleteMobileBankingAccount(user, id);

        ApiResponse<?> response = ApiResponse.<AddMobileBankingResponseDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("bank account deleted sucessfully along with the transactions")
                .timestamp(LocalDateTime.now())
                .result(deletedBankAccount)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

    }

    @GetMapping("/get-all-accounts-balance")
    @PreAuthorize("hasAuthority('Get Accounts Balance') or hasRole('OWNER')")
public ResponseEntity<ApiResponse<?>> getAllAccountBalance(@AuthenticationPrincipal User user)
    {
        AllAccountBalanceDto allAccountBalance= accountsService.getAccountsBalance(user);

        ApiResponse<?> response = ApiResponse.<AllAccountBalanceDto>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("fatched all account balance")
                .timestamp(LocalDateTime.now())
                .result(allAccountBalance)
                .error(null)
                .build();
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

}
