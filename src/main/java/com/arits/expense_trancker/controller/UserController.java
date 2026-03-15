package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final TransactionService transactionService;
    private final AccountsService accountsService;


    @GetMapping("/user-info")
    @PreAuthorize("hasAuthority('User Info') or hasAnyRole('OWNER','ADMIN')")

    public ResponseEntity<UserDetailResponseDto> getCurrentUserDetails(@AuthenticationPrincipal User currentUser) {

        UserDetailResponseDto userDetails = userService.getUserDetails(currentUser);
        return ResponseEntity.ok(userDetails);
    }


    @GetMapping("/subusers-list")
    @PreAuthorize("hasAuthority('Subuser List') or hasRole('OWNER')")
    public ResponseEntity<List<SubuserListDto>> getUserUserList(@AuthenticationPrincipal User currentUser) {

        List<SubuserListDto> subusers = userService.getSubuserList(currentUser);
        return ResponseEntity.ok(subusers);

    }

    @PostMapping("/create-subuser")
    @PreAuthorize("hasAuthority('Create Subuser') or hasRole('OWNER')")
    public ResponseEntity<UserRegisterResponseDto> registerSubUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, @AuthenticationPrincipal User user) {

        UserRegisterResponseDto userRegisterResponseDto = userService.createSubUser(userRegisterRequestDto, user.getUserId());
        return ResponseEntity.ok(userRegisterResponseDto);
    }


    @DeleteMapping("/delete-subUser/{id}")
    @PreAuthorize("hasAuthority('Delete Subusers') or hasRole('OWNER')")
    public ResponseEntity<?> deleteSubUser(@AuthenticationPrincipal User user, @PathVariable Long id) {
        userService.softDeleteSubUser(user, id);
        return ResponseEntity.ok("SubUser and associated sub-users have been soft-deleted successfully.");
    }


    @PutMapping("/update-Profile")
    @PreAuthorize("hasAuthority('Update Profile') or hasAnyRole('OWNER','SUBOWNER','ADMIN','TERTIARY')")
    public ResponseEntity<?> updateProfileDetail(@AuthenticationPrincipal User user, @RequestBody UserRegisterRequestDto userRegisterRequestDto) {


        return ResponseEntity.ok(userService.updateProfile(user, userRegisterRequestDto));
    }


    @PostMapping(value = "/add-expenses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('Add Expenses') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<ApiResponse<AddTransactionResponseDto>> addTransaction(
            @AuthenticationPrincipal User user,
            @RequestPart("data") AddTransactionRequestDto addTransactionRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        AddTransactionResponseDto newTransaction = transactionService.addTransaction(user, addTransactionRequestDTO, file);
        ApiResponse<AddTransactionResponseDto> response = ApiResponse.<AddTransactionResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Transaction added successfully")
                .timestamp(LocalDateTime.now())
                .result(newTransaction)
                .error(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    @GetMapping("/see-expenses")
    @PreAuthorize("hasAuthority('See Expenses') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<ApiResponse<?>> showExpenses(@AuthenticationPrincipal User user, @RequestBody(required = false) GetTransactionHistoryRequestDto getTransactionHistoryRequestDto) {

        GetTransactionHistoryAllDto ExpensesList = transactionService.showExpenses(user, getTransactionHistoryRequestDto);
        ApiResponse<GetTransactionHistoryAllDto> response = ApiResponse.<GetTransactionHistoryAllDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("showing expenses")
                .timestamp(LocalDateTime.now())
                .result(ExpensesList)
                .error(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);


    }


    @PutMapping(value = "/modify-expenses/{tId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('Modify Expenses') or hasRole('OWNER')")
    public ResponseEntity<?> modifyTransaction(@AuthenticationPrincipal User user,
                                               @RequestPart("data") AddTransactionRequestDto addTransactionRequestDTO,
                                               @RequestPart(value = "file", required = false) MultipartFile file,
                                               @PathVariable("tId") Long tId
    ) {


        return ResponseEntity.ok(transactionService.modifyTransaction(user, addTransactionRequestDTO, file, tId));
    }

    @DeleteMapping("/delete-transaction/{tId}")
    @PreAuthorize("hasAuthority('Delete Expenses') or hasRole('OWNER')")
    public ResponseEntity<?> deleteTransaction(@AuthenticationPrincipal User user,
                                               @PathVariable("tId") Long tId
    ) {
        return ResponseEntity.ok(transactionService.deleteTransaction(user, tId));
    }


    @GetMapping("/deleted-transaction-list")
    @PreAuthorize("hasAuthority('Deleted Expense List') or hasRole('OWNER')")
    public ResponseEntity<?> deletedTransactionList(@AuthenticationPrincipal User user) {


        return ResponseEntity.ok(transactionService.getDeletedList(user));
    }


    @PutMapping("/revive-transation/{id}")
    public ResponseEntity<?> reviveTransaction(@AuthenticationPrincipal User user, @PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.reviveTransactionFromDeath(user, id));
    }


    @PostMapping("/add-bank-account")
    @PreAuthorize("hasAuthority('Add Bank Account') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<AddBankAccountResponseDto>> addBankAccount(@AuthenticationPrincipal User user, @RequestBody AddBankAccountRequestDto addBankAccountRequestDTO) {

        AddBankAccountResponseDto newAccount = accountsService.addBankAccount(user, addBankAccountRequestDTO);

        ApiResponse<AddBankAccountResponseDto> response = ApiResponse.<AddBankAccountResponseDto>builder()
                .status(201)
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
                .status(201)
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
                .status(201)
                .message("Cash Wallet created Successfully")
                .timestamp(LocalDateTime.now())
                .result(newWallet)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }


    @GetMapping("/get-overall-balance")
    @PreAuthorize("hasAuthority('Get Overall Balance') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> getOverallBalance(@AuthenticationPrincipal User user) {

        OverallBalanceDto overallBalance = accountsService.overallBalance(user);

        ApiResponse<?> response = ApiResponse.<OverallBalanceDto>builder()
                .status(200)
                .message("successfully fetch the overall balances")
                .timestamp(LocalDateTime.now())
                .result(overallBalance)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.valueOf(200));
    }
}


