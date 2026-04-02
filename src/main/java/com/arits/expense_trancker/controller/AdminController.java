package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.security.AuthService;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AuthService authService;


    @GetMapping("/get-user-info/{key}")
    @PreAuthorize("hasAuthority('Get Any User') or hasAuthority('ADMIN')")
    public ResponseEntity<ApiResponse<?>> getUserInfo(@PathVariable("key") String keyword) {

        List<UserDetailResponseDto> userDetailResponseDtos = userService.getUserBySearch(keyword);


        ApiResponse<List<UserDetailResponseDto>> response= ApiResponse.<List<UserDetailResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("fetched user info successfully")
                .timestamp(LocalDateTime.now())
                .result(userDetailResponseDtos)
                .error(null)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }


    @DeleteMapping("/delete-user/{id}")
    @PreAuthorize("hasAuthority('Delete Users') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        DeletedUserResponseDto deletedUser= userService.softDeleteUser(id);
        ApiResponse<?> response= ApiResponse.<DeletedUserResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("User deleted successfully")
                .timestamp(LocalDateTime.now())
                .result(deletedUser)
                .error(null)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @GetMapping("/get-users-list")
    @PreAuthorize("hasAuthority('Get Users List') or hasRole('ADMIN')")
    public ResponseEntity<?> getALlUser() {
        List<AlluserListDto> usersList= userService.getAllUsers();

        ApiResponse<List<AlluserListDto>> response= ApiResponse.<List<AlluserListDto>>builder()
                .status(HttpStatus.OK.value())
                .message("fateched all users list successfully")
                .timestamp(LocalDateTime.now())
                .result(usersList)
                .error(null)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/get-deleted-users-list")
    @PreAuthorize("hasAuthority('Get Deleted Users List') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DeletedUsersListDto>>> getDeletedUsersList() {
        List<DeletedUsersListDto> deletedUsers= userService.getAllDeletedUsers();
        ApiResponse<List<DeletedUsersListDto>>response= ApiResponse.<List<DeletedUsersListDto>>builder()
                .status(HttpStatus.OK.value())
                .message("fetched deleted users list succressfully")
                .timestamp(LocalDateTime.now())
                .result(deletedUsers)
                .error(null)
                .build();
    return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/retrive-users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> retrieveUser(@PathVariable("id") Long userId) {
        RetriveUserResponseDto reviveUser= userService.retriveUser(userId);
        ApiResponse<?> response= ApiResponse.<RetriveUserResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("restore user successfully")
                .timestamp(LocalDateTime.now())
                .result(reviveUser)
                .error(null)
                .build();

        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    @PostMapping("/create-account")
    @PreAuthorize("hasAuthority('Create Subuser') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> createAccount(@RequestBody UserRegisterRequestDto userRegisterRequestDto, @AuthenticationPrincipal User user) {

        UserRegisterResponseDto userRegisterResponseDto = authService.register(userRegisterRequestDto, user);

        ApiResponse<?> response = ApiResponse.<UserRegisterResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Account Created successfully")
                .timestamp(LocalDateTime.now())
                .result(userRegisterResponseDto)
                .error(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
