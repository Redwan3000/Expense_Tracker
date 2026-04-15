package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserLoginResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.UserRepo;
import com.arits.expense_trancker.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepo userRepo;


    @PostMapping({
            "/users-register",
            "/owner/subuser-register",
            "/admin/user-register",
            "/admin/subuser-register/{ownerId}"})
    public ResponseEntity<ApiResponse<?>> registerNewuser(@AuthenticationPrincipal User user,
                                                          @RequestBody UserRegisterRequestDto requestDto,
                                                          @PathVariable(value = "ownerId", required = false) Long ownerId) {

        User passengerUser = null;

        if (user != null) {
            boolean canCreateSubowner = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("Can Create New Subowner"));
            boolean canCreateOtherSubowner = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("Can Create Subowner For Other User"));

            if (canCreateOtherSubowner && ownerId != null) {
                passengerUser = userRepo.findById(ownerId)
                        .orElseThrow(() -> new RuntimeException("Invalid OwnerId"));
            } else if (canCreateSubowner) {
                passengerUser = user;
            }
        }

        UserRegisterResponseDto registers = authService.register(passengerUser, requestDto);

        ApiResponse<?> response = ApiResponse.<UserRegisterResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Successfully Registered")
                .timestamp(LocalDateTime.now())
                .result(registers)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {

        UserLoginResponseDto login = authService.login(userLoginRequestDto);

        ApiResponse<?> response = ApiResponse.<UserLoginResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully Logged in")
                .timestamp(LocalDateTime.now())
                .result(login)
                .error(null)
                .build();

        return ResponseEntity.ok(response);
    }


}
