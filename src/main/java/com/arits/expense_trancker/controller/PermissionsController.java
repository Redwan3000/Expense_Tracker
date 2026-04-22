package com.arits.expense_trancker.controller;


import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.handler.ApiResponse;
import com.arits.expense_trancker.handler.Resolver;
import com.arits.expense_trancker.handler.Verifier;
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
    private final Verifier verifier;
    private final Resolver resolver;


    //    controller for setting roles new permission set removing the older ones for both admin and user usage
    @PostMapping({
            "/user/set-new-permissions-to-role/{roleId}",
            "/admin/set-new-permissions-to-role/{roleId}"
    })
    public ResponseEntity<ApiResponse<?>> setPermissionsToRole(@AuthenticationPrincipal User user,
                                                               @RequestBody SetPermissionDto setPermissionDto,
                                                               @PathVariable(name = "roleId") Long roleId) {

        verifier.checkRoleExistanceById(roleId);
        verifier.checkPermissionIds(setPermissionDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(setPermissionDto.getPermissionIds());

        SetPermissionDto setPermission = permissionService.setPermissionToRole(user, roleId, setPermissionDto);

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("New Permissions set to the Role successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    controller for adding roles new permission without effecting the older permissions for both admin and user usage
    @PostMapping({
            "/user/add-new-permissions-to-role/{roleId}",
            "/admin/add-new-permissions-to-role/{roleId}"
    })
    public ResponseEntity<ApiResponse<?>> addNewPermissionToRole(@AuthenticationPrincipal User user,
                                                                 @RequestBody SetPermissionDto setPermissionDto,
                                                                 @PathVariable(name = "roleId") Long roleId) {

        verifier.checkRoleExistanceById(roleId);
        verifier.checkPermissionIds(setPermissionDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(setPermissionDto.getPermissionIds());


        SetPermissionDto setPermission = permissionService.addNewPermissionToRole(user, roleId, setPermissionDto);

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("New Permission added to the Role successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    Controller for the getting the permission list assigned or active on Roles
    @GetMapping({
            "/user/roles-permission-list/{roleId}",
            "/admin/roles-permission-list/{roleId}"})
    public ResponseEntity<ApiResponse<?>> rolesActivePermissionList(@AuthenticationPrincipal User user,
                                                                    @PathVariable("roleId") Long roleId) {

        verifier.checkRoleExistanceById(roleId);

        List<RolesPermissionsRepsonseDto> permissionList = permissionService.rolesPermissionList(user, roleId);

        ApiResponse<?> response = ApiResponse.<List<RolesPermissionsRepsonseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Roles Active Permissions list fetched successfully")
                .timestamp(LocalDateTime.now())
                .result(permissionList)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    Controller for the getting the users active permission list
    @GetMapping({
            "/user/users-active-permission-List",
            "/user/subuser-active-permission-List/{subuserId}",
            "/admin/users-active-permission-List/{userId}",
            "/admin/subusers-active-permission-List/{userId}/{subuser}"
    })
    public ResponseEntity<ApiResponse<?>> usersActivePermission(@AuthenticationPrincipal User user,
                                                                @PathVariable(value = "userId", required = false) Long userId,
                                                                @PathVariable(value = "subuserId", required = false) Long subuserId) {

        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);

        List<RolesPermissionsRepsonseDto> permissionList = permissionService.usersActivePermissionsList(user, userId, subuserId);

        ApiResponse<?> response = ApiResponse.<List<RolesPermissionsRepsonseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("User Active Permissions list fetched successfully")
                .timestamp(LocalDateTime.now())
                .result(permissionList)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    controller for removing permissions from the role
    @DeleteMapping({
            "/user/remove-permissions-from-role/{roleId}",
            "/admin/remove-permissions-from-role/{roleId}"
    })
    public ResponseEntity<ApiResponse<?>> removePermissionsFromRole(@AuthenticationPrincipal User user,
                                                                    @RequestBody SetPermissionDto setPermissionDto,
                                                                    @PathVariable(name = "roleId") Long roleId) {

        verifier.checkRoleExistanceById(roleId);
        verifier.checkPermissionIdsExistsInRole(roleId, setPermissionDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(setPermissionDto.getPermissionIds());


        SetPermissionDto setPermission = permissionService.removePermissionFromRole(user, roleId, setPermissionDto);

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("New Permission added to the Role successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }

    //    controller for removing permissions from the role
    @DeleteMapping({
            "/user/remove-permissions-from-subuser/{subuserId}",
            "/admin/remove-permissions-from-user/{userId}",
            "/admin/remove-permissions-from-subuser/{userId}/{subuserId}"
    })
    public ResponseEntity<ApiResponse<?>> removePermissionsFromUser(@AuthenticationPrincipal User user,
                                                                    @RequestBody SetPermissionDto setPermissionDto,
                                                                    @PathVariable(name = "userId", required = false) Long userId,
                                                                    @PathVariable(name = "subuserId", required = false) Long subuserId) {
        verifier.checkUserExistence(userId);
        verifier.checkUserExistence(subuserId);
        verifier.checkPermissionIdsEmptyOrNot(setPermissionDto.getPermissionIds());
        verifier.checkPermissionIdsExistsInUser(resolver.getTargetUserId(user, userId, subuserId), setPermissionDto.getPermissionIds());

        if (user.getId().equals(userId) || user.getId().equals(subuserId)) {
            throw new RuntimeException("You cannot remove your own permissions");
        }

        SetPermissionDto setPermission = permissionService.removePermissionFromUser(user, userId, subuserId, setPermissionDto);

        ApiResponse<?> response = ApiResponse.<SetPermissionDto>builder()
                .status(HttpStatus.OK.value())
                .message("permission removed from the user successfully")
                .timestamp(LocalDateTime.now())
                .result(setPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


}
