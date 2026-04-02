package com.arits.expense_trancker.dto;

import com.arits.expense_trancker.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserInfoDto {

    private Long id;
    private String userName;
    private String name;
    private String role;
    private String gender;
    private String phone;
    private LocalDate dob;

}
