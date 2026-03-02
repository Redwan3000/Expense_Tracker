package com.arits.expense_trancker.dto;

import lombok.*;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeletedUsersListDto {
    private Long id;
    private String username;
    private LocalDateTime deletedAt;
}
