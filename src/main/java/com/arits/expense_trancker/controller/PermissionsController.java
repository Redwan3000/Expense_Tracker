package com.arits.expense_trancker.controller;


import com.arits.expense_trancker.dto.PermissionRequestDto;
import com.arits.expense_trancker.dto.SetPermissionDto;
import com.arits.expense_trancker.dto.UpdatePermissionRequestDto;
import com.arits.expense_trancker.dto.UpdatePermssionResponseDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.service.PermissionsService;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/permissions")
public class PermissionsController {

    private final UserService userService;
    private final PermissionsService permissionsService;

    //modifying role's permission
    @PostMapping("/modify-roles-permission")
    @PreAuthorize("hasAuthority('modify role permission') or hasRole('ADMIN')")
    public ResponseEntity<SetPermissionDto> addPermissionsToRole(@RequestBody SetPermissionDto setPermissionDto) {

        return ResponseEntity.ok(permissionsService.assigningPermissions(
                setPermissionDto.getRoleId(),
                setPermissionDto.getPermissionId()));
    }

    @PostMapping("/modify-subusers-permission")
    @PreAuthorize("hasAuthority('modify subusers permission') or hasRole('ADMIN')")
    public ResponseEntity<SetPermissionDto> addPermissionsTosubUsersRole(@AuthenticationPrincipal User user, @RequestBody SetPermissionDto setPermissionDto) {

        return ResponseEntity.ok(permissionsService.setPermissionSubUser(user,setPermissionDto.getRoleId(),setPermissionDto.getPermissionId()));
    }



//
//    //deleteing roles permission
//    @DeleteMapping("/modify-roles-permission")
//    @PreAuthorize("hasAuthority('modify role permission')")
//    public ResponseEntity<?> deletePermissionFromRole(@RequestBody SetPermissionDto setPermissionDto) {
//
//        return ResponseEntity.ok(permissionsService.deletePermissionFromRole(
//                setPermissionDto.getRoleName(),
//                setPermissionDto.getPermissionId()));
//    }

    //updating or replaceing roles permissions
    @PutMapping("/modify-roles-permission")
    @PreAuthorize("hasAuthority('modify subuser permission')")
    public ResponseEntity<UpdatePermssionResponseDto> updatePermissionsToRole(@RequestBody UpdatePermissionRequestDto updatePermissionRequestDto) {

        return ResponseEntity.ok(permissionsService.updatePermission(
                updatePermissionRequestDto.getRoleName(),
                updatePermissionRequestDto.getReplacedTo(),
                updatePermissionRequestDto.getReplacedWith()));
    }


//    //adding users specific permission
//    @PostMapping("/modify-users-permission")
//    @PreAuthorize("hasAuthority('modify user permission')")
//    public ResponseEntity<?> modifyUsersPermission(@RequestBody SetPermissionDto setPermissionDto) {
//        return ResponseEntity.ok(permissionsService.assigningPermissionsToUser(
//                setPermissionDto.getId(),
//                setPermissionDto.getRoleName(),
//                setPermissionDto.getPermissionId()));
//    }
//    //deleteing user permission
//    @DeleteMapping("/modify-users-permission")
//    @PreAuthorize("hasAuthority('modify user permission')")
//    public ResponseEntity<?> deletePermissionFromUser(@RequestBody SetPermissionDto setPermissionDto) {
//
//        return ResponseEntity.ok(permissionsService.deletePermissionFromUser(
//                setPermissionDto.getId(),
//                setPermissionDto.getPermissionId()));
//    }


    @GetMapping("/modify-permissions")
    @PreAuthorize("hasAuthority('see permission list')")
    public ResponseEntity<?> permissionList(@RequestBody PermissionRequestDto permissionRequestDto) {
        return ResponseEntity.ok(permissionsService.permissionList(permissionRequestDto.getRoleId()));
    }


}
