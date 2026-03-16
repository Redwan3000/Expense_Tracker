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

    public ResponseEntity<ApiResponse<?>> getCurrentUserDetails(@AuthenticationPrincipal User currentUser) {

        UserDetailResponseDto userDetails = userService.getUserDetails(currentUser);

        ApiResponse<UserDetailResponseDto> response = ApiResponse.<UserDetailResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("User info fetched")
                .timestamp(LocalDateTime.now())
                .result(userDetails)
                .error(null)
                .build();


        return new ResponseEntity<>(response, HttpStatus.OK);


    }


    @GetMapping("/subusers-list")
    @PreAuthorize("hasAuthority('Subuser List') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<SubuserListDto>>> getUserUserList(@AuthenticationPrincipal User currentUser) {

        List<SubuserListDto> subusers = userService.getSubuserList(currentUser);

        ApiResponse<List<SubuserListDto>> responses = ApiResponse.<List<SubuserListDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Fetched user's subUsers list")
                .timestamp(LocalDateTime.now())
                .result(subusers)
                .error(null)
                .build();
        return new ResponseEntity<>(responses, HttpStatus.CREATED);


    }

    @PostMapping("/create-subuser")
    @PreAuthorize("hasAuthority('Create Subuser') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> registerSubUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, @AuthenticationPrincipal User user) {

        UserRegisterResponseDto userRegisterResponseDto = userService.createSubUser(userRegisterRequestDto, user.getUserId());

        ApiResponse<?> response = ApiResponse.<UserRegisterResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("SubUser Created successfully")
                .timestamp(LocalDateTime.now())
                .result(userRegisterResponseDto)
                .error(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping("/delete-subUser/{id}")
    @PreAuthorize("hasAuthority('Delete Subusers') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> deleteSubUser(@AuthenticationPrincipal User user, @PathVariable Long id) {
        DeletedUserResponseDto deleteSubuser = userService.softDeleteSubUser(user, id);
        ApiResponse<?> response = ApiResponse.<DeletedUserResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("SubUser deleted successfully")
                .timestamp(LocalDateTime.now())
                .result(deleteSubuser)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/update-Profile")
    @PreAuthorize("hasAuthority('Update Profile') or hasAnyRole('OWNER','SUBOWNER','ADMIN','TERTIARY')")
    public ResponseEntity<?> updateProfileDetail(@AuthenticationPrincipal User user, @RequestBody UserRegisterRequestDto userRegisterRequestDto) {

        UserRegisterRequestDto updateProfile = userService.updateProfile(user, userRegisterRequestDto);

        ApiResponse<?> response= ApiResponse.<UserRegisterRequestDto>builder()
                .status(HttpStatus.OK.value())
                .message("User profile updated Successfully")
                .timestamp(LocalDateTime.now())
                .result(updateProfile)
                .error(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
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


    @PutMapping(value = "/modify-expenses/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('Modify Expenses') or hasRole('OWNER')")
    public ResponseEntity<?> modifyTransaction(@AuthenticationPrincipal User user,
                                               @RequestPart("data") AddTransactionRequestDto addTransactionRequestDTO,
                                               @RequestPart(value = "file", required = false) MultipartFile file,
                                               @PathVariable("id") Long tId
    ) {

        AddTransactionResponseDto modifyExpense= transactionService.modifyTransaction(user, addTransactionRequestDTO, file, tId);

        ApiResponse<?> response= ApiResponse.<AddTransactionResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("transaction modified successfully")
                .timestamp(LocalDateTime.now())
                .result(modifyExpense)
                .error(null)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @DeleteMapping("/delete-transaction/{id}")
    @PreAuthorize("hasAuthority('Delete Expenses') or hasRole('OWNER')")
    public ResponseEntity<?> deleteTransaction(@AuthenticationPrincipal User user,
                                               @PathVariable("id") Long tId
    ) {

        DeleteTransactionResponseDTO deteTransaction= transactionService.deleteTransaction(user, tId);

        ApiResponse<?> response= ApiResponse.<DeleteTransactionResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("deleted transaction successfully")
                .timestamp(LocalDateTime.now())
                .result(deteTransaction)
                .error(null)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/deleted-transaction-list")
    @PreAuthorize("hasAuthority('Deleted Expense List') or hasRole('OWNER')")
    public ResponseEntity<?> deletedTransactionList(@AuthenticationPrincipal User user) {

        List<DeletedExpensesResponseDto> deletedTransactionList= transactionService.getDeletedList(user);

    ApiResponse<List<DeletedExpensesResponseDto>> response= ApiResponse.<List<DeletedExpensesResponseDto>>builder()
            .status(HttpStatus.OK.value())
            .message("fetched deleted transaction list successfully")
            .timestamp(LocalDateTime.now())
            .result(deletedTransactionList)
            .error(null)
            .build();
    return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @PutMapping("/revive-transation/{id}")
    public ResponseEntity<?> reviveTransaction(@AuthenticationPrincipal User user, @PathVariable("id") Long tId) {

        ReviveTransactionResponseDto revivedTransaction= transactionService.reviveTransactionFromDeath(user, tId);
        ApiResponse<?>response= ApiResponse.<ReviveTransactionResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("revived transaction successfully")
                .timestamp(LocalDateTime.now())
                .result(revivedTransaction)
                .error(null)
                .build();

    return ResponseEntity.ok(response);
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


