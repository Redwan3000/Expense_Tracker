package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.BankAccountRepo;
import com.arits.expense_trancker.security.AuthService;
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
    private final AuthService authService;

    @GetMapping("/user-info")
    @PreAuthorize("hasAuthority('User Info') or hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<?>> getCurrentUserDetails(@AuthenticationPrincipal User currentUser) {

        GetUserInfoDto userDetails = userService.getUserDetails(currentUser);

        ApiResponse<?> response = ApiResponse.<GetUserInfoDto>builder()
                .status(HttpStatus.OK.value())
                .message("User info fetched")
                .timestamp(LocalDateTime.now())
                .result(userDetails)
                .error(null)
                .build();

        return ResponseEntity.ok(response);

    }


    @GetMapping("/subusers-list")
    @PreAuthorize("hasAuthority('Subuser List') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<List<SubuserListDto>>> getUserUserList(@AuthenticationPrincipal User currentUser) {

        List<SubuserListDto> subusers = userService.getSubuserList(currentUser);

        ApiResponse<List<SubuserListDto>> responses = ApiResponse.<List<SubuserListDto>>builder()
                .status(HttpStatus.CREATED.value())
                .message("Fetched User's SubUsers List")
                .timestamp(LocalDateTime.now())
                .result(subusers)
                .error(null)
                .build();
        return new ResponseEntity<>(responses, HttpStatus.CREATED);

    }

    @PostMapping("/create-subuser")
    @PreAuthorize("hasAuthority('Create Subuser') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> registerSubUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, @AuthenticationPrincipal User user) {

        UserRegisterResponseDto userRegisterResponseDto = authService.register(userRegisterRequestDto, user);

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




}


