package com.arits.expense_trancker.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RetriveUserResponseDto {

    private Long userId;
    private String username;

}
