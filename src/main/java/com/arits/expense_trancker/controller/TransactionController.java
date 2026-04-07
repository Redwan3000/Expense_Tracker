package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.BankAccountRepo;
import com.arits.expense_trancker.service.AccountsService;
import com.arits.expense_trancker.service.TransactionService;
import lombok.RequiredArgsConstructor;
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
public class TransactionController {



    private final TransactionService transactionService;


//fixed but gets -balance
    @PostMapping(value = "/add-transaction", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
                .status(HttpStatus.NO_CONTENT.value())
                .message("deleted transaction successfully")
                .timestamp(LocalDateTime.now())
                .result(deteTransaction)
                .error(null)
                .build();
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }


    @GetMapping("/deleted-transaction-list")
    @PreAuthorize("hasAuthority('Deleted Expense List') or hasRole('OWNER')")
    public ResponseEntity<?> deletedTransactionList(@AuthenticationPrincipal User user) {

        List<DeletedExpensesResponseDto> deletedTransactionList= transactionService.getDeletedList(user);

        ApiResponse<List<DeletedExpensesResponseDto>> response= ApiResponse.<List<DeletedExpensesResponseDto>>builder()
                .status(HttpStatus.CREATED.value())
                .message("fetched deleted transaction list successfully")
                .timestamp(LocalDateTime.now())
                .result(deletedTransactionList)
                .error(null)
                .build();
        return new ResponseEntity<>(response,HttpStatus.CREATED);
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


//
//    @GetMapping("/get-overall-balance")
//    @PreAuthorize("hasAuthority('Get Overall Balance') or hasRole('OWNER')")
//    public ResponseEntity<ApiResponse<?>> getOverallBalance(@AuthenticationPrincipal User user) {
//
//        OverallBalanceDto overallBalance = accountsService.overallBalance(user);
//
//        ApiResponse<?> response = ApiResponse.<OverallBalanceDto>builder()
//                .status(200)
//                .message("successfully fetch the overall balances")
//                .timestamp(LocalDateTime.now())
//                .result(overallBalance)
//                .error(null)
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.valueOf(200));
//    }


}
