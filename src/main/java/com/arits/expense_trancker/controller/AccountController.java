package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.CashWalletRepo;
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


    //For creating cash Account
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
    public ResponseEntity<?> modifyCashWalletDetails(@AuthenticationPrincipal User user , @RequestBody ModifyCashWalletDetailsDto modifyCashWalletDetailsDto){

        CreateCashAccountResponseDto modifyCashWallet = accountsService.modifyCashWallet(user,modifyCashWalletDetailsDto);

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






//Controller For Getting Account Balance

    //Detailed Account Balance
    @GetMapping("/get-all-accounts-balance-group-wise")
    @PreAuthorize("hasAuthority('Get Accounts Balance') or hasRole('OWNER')")
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


    @GetMapping("/get-bank-account-Details/{id}")
    @PreAuthorize("hasAuthority('Get Bank Account Detail') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getAccountBalanceGroupWise(
            @AuthenticationPrincipal User user,
            @PathVariable(value = "id",required = false) Long id) {

        List<BankAccountsBalanceDto> accountBalance = accountsService.getBankAccountDetails(user, id);

        ApiResponse<?> apiResponse = ApiResponse.<List<BankAccountsBalanceDto> >builder()
                .status(HttpStatus.OK.value())
                .message("Fetched all account balance")
                .timestamp(LocalDateTime.now())
                .result(accountBalance)
                .error(null)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
