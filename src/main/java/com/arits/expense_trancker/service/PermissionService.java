package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {


    public final PermissionRepo permissionRepo;
    public final RoleRepo roleRepo;
    public final UserRepo userRepo;
    public final RolesDefaultPermissionsRepo rolesDefaultPermissionsRepo;
    public final UsersPermissionsRepo usersPermissionsRepo;


    public void permissionsSeeding(String name, String des) {
        Optional<Permission> existingPermission = permissionRepo.findByNameIncludingDeleted(name);
        if (existingPermission.isPresent()) {
            Permission p = existingPermission.get();
            if (p.isDeleted()) {
                p.setDeleted(false);
                permissionRepo.save(p);
            }
        } else {

            permissionRepo.save(Permission.builder()
                    .name(name)
                    .description(des)
                    .isDeleted(false)
                    .build());
        }

    }


    @Transactional
    public SetPermissionDto setPermissionToRole(Long roleId, List<Long> permissionIds) {

        Set<Long> validPermissionsIds = permissionRepo.getPermissionID(permissionIds);

        rolesDefaultPermissionsRepo.softdeleteOldPermissions(roleId, validPermissionsIds);

        Set<Long> newPermissionsToAdd = permissionRepo.getNewPermissionsToAdd(roleId, validPermissionsIds);

        rolesDefaultPermissionsRepo.setNewPermissions(roleId, newPermissionsToAdd);

        List<Long> userIds = userRepo.findUsersIdByRoleId(roleId);

        if (!userIds.isEmpty()) {
            usersPermissionsRepo.softDeleteUsersOldPermissions(userIds, validPermissionsIds);
            usersPermissionsRepo.setUsersPermissions(roleId, validPermissionsIds);
        }


        return new SetPermissionDto(roleId, permissionIds);
    }


    public List<PermissionResponseDto> permissionList(Long roleId) {

        List<PermissionResponseDto> response = rolesDefaultPermissionsRepo.findRolesPermission(roleId);

        return response;
    }

//
//    @Transactional
//    public UpdatePermssionResponseDto updatePermission(String roleName, List<Long> replacedTo, List<Long> replacedWith) {
//
//        Role role = roleRepo.findByName(roleName)
//                .orElseThrow(() -> new IllegalArgumentException("role does not exist"));
//
//        Set<RolesDefaultPermissions> validateNewPermission = replacedWith.stream()
//                .map(p -> permissionRepo.findById(p).orElseThrow(() -> new IllegalArgumentException("permission does not exist")))
//                .map(permission -> RolesDefaultPermissions.builder()
//                        .role(role)
//                        .permission(permission)
//                        .isDeleted(false)
//                        .build())
//                .collect(Collectors.toSet());
//
//        Set<RolesDefaultPermissions> validateOldPermission = role.getRolesDefaultPermissions().stream()
//                .filter(rolePerm -> replacedTo.contains(rolePerm.getPermission().getId()))
//                .collect(Collectors.toSet());
//
//
//        role.getRolesDefaultPermissions().removeAll(validateOldPermission);
//        role.getRolesDefaultPermissions().addAll(validateNewPermission);
//
//        List<Long> updatedPermissionIds = role.getRolesDefaultPermissions().stream()
//                .map(p -> p.getPermission().getId())
//                .collect(Collectors.toList());
//
//        return new UpdatePermssionResponseDto(roleName, updatedPermissionIds);
//    }


    @Transactional
    public SetPermissionDto setPermissionToSubusersRole(User user, SetPermissionDto setPermissionDto) {

        Set<Long> validPermissionsIds = permissionRepo.getPermissionID(setPermissionDto.getPermissionId());

        usersPermissionsRepo.softdeleteOldPermissionsForSubusers(user.getId(), setPermissionDto.getRoleId(), validPermissionsIds);

        Set<Long> newPermissionsToAdd = permissionRepo.getNewPermissionsToAdd(setPermissionDto.getRoleId(), validPermissionsIds);


        usersPermissionsRepo.setSubUsersPermissions(user.getId(), setPermissionDto.getRoleId(), newPermissionsToAdd);


        return new SetPermissionDto(setPermissionDto.getRoleId(), setPermissionDto.getPermissionId());
    }



    public List<PermissionResponseDto> subUsersPermission(User user, Long roleId) {

        List<PermissionResponseDto> response = usersPermissionsRepo.findSubUserRolesPermission(user.getId(),roleId);

        return response;
    }
}
