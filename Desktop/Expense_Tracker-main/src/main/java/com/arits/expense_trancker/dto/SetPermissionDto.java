package com.arits.expense_trancker.dto;

import com.arits.expense_trancker.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SetPermissionDto {

    private Long roleId;
    private List<Long> permissionId;




}
