package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final userRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final genderRepo genderRepo;
    private final roleRepo roleRepo;


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

    @PostMapping("/add-transaction")
    public ResponseEntity<?>addTransaction(@AuthenticationPrincipal User user , @RequestBody AddTransactionRequestDTO addTransactionRequestDTO){

        return ResponseEntity.ok(userService.addTransaction(user, addTransactionRequestDTO));


    }

}


