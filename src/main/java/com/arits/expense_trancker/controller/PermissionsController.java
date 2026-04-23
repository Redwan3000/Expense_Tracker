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
            "/user/set-new-permissions-to-role",
            "/admin/set-new-permissions-to-role"
    })
    public ResponseEntity<ApiResponse<?>> setPermissionsToRole(@AuthenticationPrincipal User user,
                                                               @RequestBody SetPermissionDto requestDto) {


        verifier.checkRoleExistanceById(requestDto.getRoleId());
        verifier.checkPermissionIds(requestDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(requestDto.getPermissionIds());

        SetPermissionDto setPermission = permissionService.setPermissionToRole(user, requestDto);

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
            "/user/add-new-permissions-to-role",
            "/admin/add-new-permissions-to-role"
    })
    public ResponseEntity<ApiResponse<?>> addNewPermissionToRole(@AuthenticationPrincipal User user,
                                                                 @RequestBody SetPermissionDto requestDto) {

        verifier.checkRoleExistanceById(requestDto.getRoleId());
        verifier.checkPermissionIds(requestDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(requestDto.getPermissionIds());


        SetPermissionDto setPermission = permissionService.addNewPermissionToRole(user, requestDto);

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
                                                                    @PathVariable Long roleId) {

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
                                                                @PathVariable(required = false) Long userId,
                                                                @PathVariable(required = false) Long subuserId) {

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
                                                                    @RequestBody SetPermissionDto setPermissionDto) {

        verifier.checkRoleExistanceById(setPermissionDto.getRoleId());
        verifier.checkPermissionIdsExistsInRole(setPermissionDto.getRoleId(), setPermissionDto.getPermissionIds());
        verifier.checkPermissionIdsEmptyOrNot(setPermissionDto.getPermissionIds());


        SetPermissionDto setPermission = permissionService.removePermissionFromRole(user, setPermissionDto);

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
                                                                    @PathVariable(required = false) Long userId,
                                                                    @PathVariable(required = false) Long subuserId) {

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


    //    controller for adding roles new permission without effecting the older permissions for both admin and user usage
    @PostMapping("/admin/add-new-permission")
    public ResponseEntity<ApiResponse<?>> addNewPermission(@RequestBody AddNewPermissionRequestDto requestDto) {

        verifier.checkIsNamesNullOrNot(requestDto.getName());

        PermissionResponseDto addNewPermission = permissionService.addNewPermission(requestDto.getName(), requestDto.getDescription());

        ApiResponse<?> response = ApiResponse.<PermissionResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("New Permission added to the System Successfully")
                .timestamp(LocalDateTime.now())
                .result(addNewPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    controller for adding roles new permission without effecting the older permissions for both admin and user usage
    @PutMapping("/admin/modify-permission/{permissionId}")
    public ResponseEntity<ApiResponse<?>> modifyPermission(@PathVariable(required = false) Long permissionId,
                                                           @RequestBody AddNewPermissionRequestDto requestDto) {

        verifier.checkPermissionExistanceById(permissionId);
        verifier.checkIsNamesNullOrNot(requestDto.getName());

        PermissionResponseDto addNewPermission = permissionService.modifyPermission(permissionId, requestDto);

        ApiResponse<?> response = ApiResponse.<PermissionResponseDto>builder()
                .status(HttpStatus.OK.value())
                .message("Permission modified Successfully")
                .timestamp(LocalDateTime.now())
                .result(addNewPermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    controller for softdelete permission
    @PutMapping("/admin/delete-permission/{permissionId}")
    public ResponseEntity<ApiResponse<?>> softDeletingPermission(@PathVariable Long permissionId) {

        verifier.checkPermissionExistanceById(permissionId);

        HardDeletePermissionResponse softDeletePermission = permissionService.softDeletePermission(permissionId);

        ApiResponse<?> response = ApiResponse.<HardDeletePermissionResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Permission deleted Successfully")
                .timestamp(LocalDateTime.now())
                .result(softDeletePermission)
                .error(null)
                .build();
        return ResponseEntity.ok(response);
    }


    //    controller for Hard delete any permission
    @PutMapping("/admin/hard-delete-permission/{permissionId}")
    public ResponseEntity<ApiResponse<?>> hardDeletePermission(@AuthenticationPrincipal User user,
                                                               @PathVariable Long permissionId) {


        verifier.checkPermissionInBothWorld(permissionId);

        HardDeletePermissionResponse hardDeletePermission = permissionService.hardDeletePermission(user, permissionId);

        ApiResponse<?> response = ApiResponse.<HardDeletePermissionResponse>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("Permission deleted permanently Successfully")
                .timestamp(LocalDateTime.now())
                .result(hardDeletePermission)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }


    //    controller for Hard delete all permission
    @PutMapping("/admin/hard-delete-all-permission")
    public ResponseEntity<ApiResponse<?>> hardDeleteAllPermission(@AuthenticationPrincipal User user) {


        HardDeletePermissionResponse hardDeleteAllPermission = permissionService.hardDeleteAllPermission(user);

        ApiResponse<?> response = ApiResponse.<HardDeletePermissionResponse>builder()
                .status(HttpStatus.NO_CONTENT.value())
                .message("All Permission deleted Successfully")
                .timestamp(LocalDateTime.now())
                .result(hardDeleteAllPermission)
                .error(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }


    //    controller for adding roles new permission without effecting the older permissions for both admin and user usage
    @GetMapping("/admin/search-permission ")
    public ResponseEntity<ApiResponse<?>> seachPermissionList(@RequestParam(required = false) String keyword,
                                                              @RequestParam(required = false) Long permissionId,
                                                              @RequestParam(required = false) Long userId,
                                                              @RequestParam(required = false) Long roleId,
                                                              @RequestParam(defaultValue = "false" ) boolean isDeleted,
                                                              @RequestParam(defaultValue = "0", required = false) int page,
                                                              @RequestParam(defaultValue = "50") int size
    ) {

        verifier.checkPermissionId(permissionId);
        verifier.checkRoleExistanceById(roleId);
        verifier.checkUserExistence(userId);

        PaginatedResponseDto<PermissionResponseDto> result = permissionService.searchPermission(keyword, userId, roleId, permissionId, isDeleted, page, size);
        ApiResponse<?> response = ApiResponse.<PaginatedResponseDto<PermissionResponseDto>>builder()
                .status(HttpStatus.OK.value())
                .message("Searched Accounts Fetched Succesfully")
                .timestamp(LocalDateTime.now())
                .result(result)
                .error(null)
                .build();

        return ResponseEntity.ok(response);
    }


}
