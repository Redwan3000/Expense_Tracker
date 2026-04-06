package com.arits.expense_trancker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionListResponseDto {

private long id ;
private String permissionName;
private String description;

}
