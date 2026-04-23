package com.arits.expense_trancker.Mapper;


import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.entity.Permission;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponseDto toAddNewPermissionDto(Permission permission);


    DetelePermissionResponseDto toDeletePermissionDto(Permission permission);
}
