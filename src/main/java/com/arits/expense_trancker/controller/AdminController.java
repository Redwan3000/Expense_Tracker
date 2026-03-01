package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.UserDetailResponseDto;
import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final userRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final genderRepo genderRepo;
    private final roleRepo roleRepo;


    @GetMapping("/get-user-info/{keyword}")
    @PreAuthorize("hasAuthority('get any user') or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDetailResponseDto>> getUserInfo(@PathVariable("keyword") String keyword) {

        List<UserDetailResponseDto> userDetailResponseDtos = userService.getUserBySearch(keyword);
        return ResponseEntity.ok(userDetailResponseDtos);

    }


    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAuthority('delete users') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User and associated sub-users have been soft-deleted successfully.");
    }


    @GetMapping("/get-users-list")
    @PreAuthorize("hasAuthority('get users list') or hasRole('ADMIN')")
    public ResponseEntity<?> getALlUser() {

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/get-deleted-users-list")
    @PreAuthorize("hasAuthority('get deleted users list') or hasRole('ADMIN')")
    public ResponseEntity<?> getDeletedUsersList() {

        return ResponseEntity.ok(userService.getAllDeletedUsers());
    }



}
