package com.arits.expense_trancker.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailResponseDto {

    private Long user_id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private LocalDate dob;
    private String gender;
    private String role;

}
