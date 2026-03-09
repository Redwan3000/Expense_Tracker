package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.GenderRepo;
import com.arits.expense_trancker.repository.RoleRepo;
import com.arits.expense_trancker.repository.UserRepo;
import com.arits.expense_trancker.service.TransactionService;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final GenderRepo genderRepo;
    private final RoleRepo roleRepo;
    private final TransactionService transactionService;


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
    public ResponseEntity<?> addTransaction(
            @AuthenticationPrincipal User user,
            @RequestPart("data") AddTransactionRequestDTO addTransactionRequestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        return ResponseEntity.ok(transactionService.addTransaction(user, addTransactionRequestDTO, file));

    }


    @GetMapping("/see-expenses")
    @PreAuthorize("hasAuthority('See Expenses') or hasAnyRole('OWNER','SUBOWNER')")
    public ResponseEntity<?> showExpenses(@AuthenticationPrincipal User user, @RequestBody(required = false) GetTransactionHistoryRequestDto getTransactionHistoryRequestDto) {
        return ResponseEntity.ok(transactionService.showExpenses(user, getTransactionHistoryRequestDto));
    }

    @PutMapping(value = "/modify-expenses/{tId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('Modify Expenses') or hasRole('OWNER')")
    public ResponseEntity<?> modifyTransaction(@AuthenticationPrincipal User user,
                                               @RequestPart("data") AddTransactionRequestDTO addTransactionRequestDTO,
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
    public ResponseEntity<?>reviveTransaction(@AuthenticationPrincipal User user , @PathVariable("id") Long id)
    {
        return ResponseEntity.ok(transactionService.reviveTransactionFromDeath(user, id));
    }

}


