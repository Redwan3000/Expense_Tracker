package com.arits.expense_trancker.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PermissionResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

}
