package com.arits.expense_trancker.dto;

public record RolesPermissionsRepsonseDto(Long permissionId, String name, String description) {

    public RolesPermissionsRepsonseDto{
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException(("Permission name cannot be empty"));
        }
    }

}
