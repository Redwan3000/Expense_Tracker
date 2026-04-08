package com.arits.expense_trancker.controller;


import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/permissions")
public class PermissionsController {


    private final PermissionService permissionService;


    @PostMapping("/modify-roles-permission")
    @PreAuthorize("hasAuthority('Modify Role Permission') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addPermissionsToRole(@RequestBody SetPermissionDto setPermissionDto) {
        SetPermissionDto setPermission = permissionService.setPermissionToRole(setPermissionDto.getRoleId(),
                setPermissionDto.getPermissionId());

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified roles permiossion successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }



    @PostMapping("/modify-subusers-permission")
    @PreAuthorize("hasAuthority('Modify Subuser Permission') or hasRole('OWNER')")
    public ResponseEntity<ApiResponse<?>> addPermissionsTosubUsersRole(@AuthenticationPrincipal User user, @RequestBody SetPermissionDto setPermissionDto) {

        SetPermissionDto setPermission = permissionService.setPermissionToSubusersRole(user, setPermissionDto);

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("modified subusers permiossion successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


//    //updating or replaceing roles permissions
//    @PutMapping("/modify-roles-permission")
//    @PreAuthorize("hasAuthority('Modify Subuser Permission')")
//    public ResponseEntity<ApiResponse<?>> updatePermissionsToRole(@RequestBody UpdatePermissionRequestDto updatePermissionRequestDto) {
//        UpdatePermssionResponseDto updatePermission = permissionService.updatePermission(
//                updatePermissionRequestDto.getRoleName(),
//                updatePermissionRequestDto.getReplacedTo(),
//                updatePermissionRequestDto.getReplacedWith());
//
//        ApiResponse<?> response = ApiResponse.<UpdatePermssionResponseDto>builder()
//                .status(HttpStatus.OK.value())
//                .message("modified subusers permiossion successfully")
//                .timestamp(LocalDateTime.now())
//                .result(updatePermission)
//                .error(null)
//                .build();
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/all-permission-list/{id}")
    @PreAuthorize("hasAuthority('see permission list') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> permissionList(@PathVariable("id") Long roleId) {
        List<PermissionResponseDto> permissionList = permissionService.permissionList(roleId);

        ApiResponse<?> response = ApiResponse.<List<PermissionResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("fetched permiossions list successfully")
                .timestamp(LocalDateTime.now())
                .result(permissionList)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/subuser-permission-list/{id}")
    @PreAuthorize("hasAuthority('See Subuser Permission List') or hasAnyRole('ADMIN','OWNER')")
    public ResponseEntity<?> permissionList(@AuthenticationPrincipal User user,@PathVariable("id") Long roleId) {
        List<PermissionResponseDto> subUsersPermissions = permissionService.subUsersPermission(user, roleId);

        ApiResponse<?> response = ApiResponse.<List<PermissionResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("modified roles permiossion successfully")
                .timestamp(LocalDateTime.now())
                .result(subUsersPermissions)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }

}
