package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.repository.UserRepo;
import com.arits.expense_trancker.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final Verifier verifier;


    @PostMapping({
            "/users-register",
            "/owner/subuser-register",
            "/admin/user-register",
            "/admin/subuser-register/{ownerId}"})
    public ResponseEntity<ApiResponse<?>> registerNewuser(@AuthenticationPrincipal User user,
                                                          @RequestBody UserRegisterRequestDto requestDto,
                                                          @PathVariable(value = "ownerId", required = false) Long ownerId) {

        verifier.checkUserExistence(ownerId);

        UserRegisterResponseDto registers = authService.register(user, ownerId, requestDto);

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

        String token = authService.login(userLoginRequestDto);

        ApiResponse<?> response = ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Successfully Logged in")
                .timestamp(LocalDateTime.now())
                .result(token)
                .error(null)
                .build();

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false)
                .maxAge(24 * 60 * 60)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }


}
