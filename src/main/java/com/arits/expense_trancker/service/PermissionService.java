package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.handler.Resolver;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {


    public final PermissionRepo permissionRepo;
    public final RolesDefaultPermissionsRepo rolesDefaultPermissionsRepo;
    public final UsersPermissionsRepo usersPermissionsRepo;
    private final Resolver resolver;
    private final Verifier verifier;


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

    //service for setting new sets of permissions to the role
    @Transactional
    public SetPermissionDto setPermissionToRole(User user, Long roleId, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.softDeleteUnwantedPermissions(roleId, dto.getPermissionIds());
            rolesDefaultPermissionsRepo.setNewPermissions(roleId, permissionArray);
        } else {
            userID = user.getId();
            verifier.checkPermissionIdsExistsInRole(roleId, dto.getPermissionIds());
        }

        usersPermissionsRepo.softDeleteUnwantedUsersPermissions(roleId, permissionArray, userID);
        usersPermissionsRepo.setNewRolePermissionsToUsers(roleId, permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(roleId)
                .permissionIds(dto.getPermissionIds())
                .build();

    }


    //    service for adding a new permission to the roles
    @Transactional
    public SetPermissionDto addNewPermissionToRole(User user, Long roleId, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.setNewPermissions(roleId, permissionArray);
        } else {
            userID = user.getId();

        }

        usersPermissionsRepo.setNewRolePermissionsToUsers(roleId, permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(roleId)
                .permissionIds(dto.getPermissionIds())
                .build();
    }


    //    service for getting the roles active PermissionsList
    public List<RolesPermissionsRepsonseDto> rolesPermissionList(User user, Long roleId) {

        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isHeAdmin) {
            return usersPermissionsRepo.findRolesActivePermissions(roleId, null);
        } else if (!roleId.equals(user.getRole().getId())) {
            verifier.checkUserExistanceAsParentOfRole(user.getId(), roleId);
        }

        return usersPermissionsRepo.findRolesActivePermissions(roleId, user.getId());
    }


    //    service for getting the user active permission list
    public List<RolesPermissionsRepsonseDto> usersActivePermissionsList(User user, Long userId, Long subuserId) {

        Long targetId = resolver.getTargetUserId(user, userId, subuserId);
        return usersPermissionsRepo.getUsersActivePermissionList(targetId);

    }


    @Transactional
//service for removing permissions from role
    public SetPermissionDto removePermissionFromRole(User user, Long roleId, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.softDeleteUnwantedPermissions(roleId, dto.getPermissionIds());
        } else {
            userID = user.getId();
        }
        usersPermissionsRepo.softDeleteUnwantedUsersPermissions(roleId, permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(roleId)
                .permissionIds(dto.getPermissionIds())
                .build();

    }


    public SetPermissionDto removePermissionFromUser(User user, Long userId, Long subuserId, SetPermissionDto dto) {

        Long targetUserid = resolver.getTargetUserId(user, userId, subuserId);
        usersPermissionsRepo.softDeleteUsersPermissions(targetUserid, dto.getPermissionIds());

        return SetPermissionDto.builder()
                .permissionIds(dto.getPermissionIds())
                .build();

    }
}
