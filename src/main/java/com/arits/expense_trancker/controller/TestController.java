package com.arits.expense_trancker.controller;


import com.arits.expense_trancker.dto.testingDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {


    private final UserRepo userRepo;

//    @GetMapping("/testing")
//    @PreAuthorize("hasRole('OWNER')")
//    public ResponseEntity<ApiResponse<?>> testing(@AuthenticationPrincipal User user)
//        {
//
//            List<testing> result= userRepo.getUserBankDetails(user.getId());
//            ApiResponse<?> response= ApiResponse.<List<testing>>builder()
//                    .status(HttpStatus.OK.value())
//                    .message("got it")
//                    .timestamp(LocalDateTime.now())
//                    .result(result)
//                    .error(null)
//                    .build();
//
//
//            return ResponseEntity.ok(response);
//        }


    @GetMapping("/testing")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> testing(@AuthenticationPrincipal User user)
    {

        List<Object[]> result= userRepo.getUserBankDetails(user.getId());


        List<testingDto> result2= result.stream().map(m->testingDto.builder()
                .userId((Long) m[0])
                .userName((String) m[1])
                .bankName((String) m[2])
                .accountType((String) m[3])
                .build()).toList();


        ApiResponse<?> response= ApiResponse.<List<testingDto>>builder()
                .status(HttpStatus.OK.value())
                .message("got it")
                .timestamp(LocalDateTime.now())
                .result(result2)
                .error(null)
                .build();


        return ResponseEntity.ok(response);
    }


}
