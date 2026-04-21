package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public SetPermissionDto setPermissionToRole(Long roleId, SetPermissionDto dto) {
        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);

        rolesDefaultPermissionsRepo.softDeleteUnwantedPermissions(roleId, dto.getPermissionIds());
        rolesDefaultPermissionsRepo.setNewPermissions(roleId, permissionArray);

        usersPermissionsRepo.softDeleteUnwantedUsersPermissions(roleId, permissionArray);
        usersPermissionsRepo.setNewRolePermissionsToUsers(roleId, permissionArray);

        return SetPermissionDto.builder()
                .permissionIds(dto.getPermissionIds())
                .build();
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

        Set<Long> validPermissionsIds = permissionRepo.getPermissionID(setPermissionDto.getPermissionIds());

        usersPermissionsRepo.softdeleteOldPermissionsForSubusers(user.getId(), setPermissionDto.getRoleId(), validPermissionsIds);

        Set<Long> newPermissionsToAdd = permissionRepo.getNewPermissionsToAdd(setPermissionDto.getRoleId(), validPermissionsIds);


        usersPermissionsRepo.setSubUsersPermissions(user.getId(), setPermissionDto.getRoleId(), newPermissionsToAdd);


        return new SetPermissionDto(setPermissionDto.getRoleId(), setPermissionDto.getPermissionIds());
    }


    public List<PermissionResponseDto> subUsersPermission(User user, Long roleId) {

        List<PermissionResponseDto> response = usersPermissionsRepo.findSubUserRolesPermission(user.getId(), roleId);

        return response;
    }
}
