package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserLoginResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
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

//fixed
    @PostMapping("/users-register")
    public ResponseEntity<ApiResponse<?>> UsersRegister(@AuthenticationPrincipal User user, @RequestBody UserRegisterRequestDto userRegisterRequestDto) {

        UserRegisterResponseDto registers = authService.register(userRegisterRequestDto, user);

        ApiResponse<?> response = ApiResponse.<UserRegisterResponseDto>builder()
                .status(HttpStatus.CREATED.value())
                .message("Successfully Registered")
                .timestamp(LocalDateTime.now())
                .result(registers)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

//fixed
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
