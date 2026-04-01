package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequestDto {


    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String password;
    private LocalDate dob;
    private Long gender_id;
    private String username;
    private Long role_id;

}
