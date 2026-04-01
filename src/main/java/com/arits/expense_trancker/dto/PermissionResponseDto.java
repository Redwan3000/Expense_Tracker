package com.arits.expense_trancker.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionResponseDto {

private Long id;
private String permissionName;
private String Description;


}
