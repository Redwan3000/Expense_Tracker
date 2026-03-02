package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserLoginResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.repository.userRepo;
import com.arits.expense_trancker.security.AuthService;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;
    private final UserService userService;

    private final userRepo userRepo;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponseDto> register(@RequestBody UserRegisterRequestDto userRegisterRequestDto) {

        return ResponseEntity.ok(authService.register(userRegisterRequestDto));
    }


    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        return ResponseEntity.ok(authService.login(userLoginRequestDto));
    }


}
