package com.arits.expense_trancker.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePermssionResponseDto {


    private String roleName;
    private List<Long> permissionIds;



}

