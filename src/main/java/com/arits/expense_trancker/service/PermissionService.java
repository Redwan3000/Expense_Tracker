package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.PermissionRequestDto;
import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.dto.SetPermissionDto;
import com.arits.expense_trancker.dto.UpdatePermssionResponseDto;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                    .permissionName(name)
                    .description(des)
                    .isDeleted(false)
                    .build());
        }

    }



    @Transactional
    public SetPermissionDto assigningPermissions(Long roleId, List<Long> permissionIds) {
        Role role = roleRepo.findById(roleId).orElseThrow(() -> new RuntimeException("role not found"));
        List<RolesDefaultPermissions> currentPermissions = rolesDefaultPermissionsRepo.findCurrentPermissionsByRoleId(roleId);

        currentPermissions.forEach(permission -> {

            boolean status = permissionIds.contains(permission.getPermission().getId());
            permission.setDeleted(!status);
            permission.setDeletedAt(!status ? LocalDateTime.now() : null);

        });
        Set<Long> oldPermissionId = currentPermissions.stream().map(r -> r.getPermission().getId()).collect(Collectors.toSet());

        permissionIds.stream().filter(id -> !oldPermissionId.contains(id)).forEach(newId -> {

            Permission p = permissionRepo.findById(newId).orElseThrow(() -> new RuntimeException("permission not found"));
            rolesDefaultPermissionsRepo.save(RolesDefaultPermissions.builder()
                    .role(role)
                    .permission(p)
                    .isDeleted(false)
                    .build());

        });
        rolesDefaultPermissionsRepo.flush();
        List<User> allUsers= userRepo.findUsersByRole(role);

        for (User user : allUsers) {

            List<UsersPermissions> usersPermissions = usersPermissionsRepo.findPermissionsByUserId(user.getId());

            Set<Long> usersPermissionsIds = usersPermissions.stream()
                    .map(p -> p.getPermission().getId())
                    .collect(Collectors.toSet());


            usersPermissions.stream()
                    .filter(up -> !permissionIds.contains(up.getPermission().getId()))
                    .forEach(up -> {
                        up.setDeleted(true);
                        up.setDeletedAt(LocalDateTime.now());
                    });

            usersPermissions.stream()
                    .filter(up -> permissionIds.contains(up.getPermission().getId()))
                    .forEach(up -> {up.setDeleted(false);
                        up.setDeletedAt(null);
                    } );

            permissionIds.stream()
                    .filter(id -> !usersPermissionsIds.contains(id))
                    .forEach(newId -> {
                        Permission permission = permissionRepo.findById(newId)
                                .orElseThrow(() -> new RuntimeException("permission not found"));

                        usersPermissionsRepo.save(UsersPermissions.builder()
                                .user(user)
                                .permission(permission)
                                .isDeleted(false)
                                .build());
                    });

        }

        return new SetPermissionDto(roleId, permissionIds);
    }

    public List<PermissionResponseDto> permissionList(Long roleId) {

        List<Permission> permissionsByRole = permissionRepo.findPermissionsByRoleId(roleId);

        if (!permissionsByRole.isEmpty()) {

            return permissionRepo.findPermissionsByRoleId(roleId)
                    .stream()
                    .map(
                            Permission -> new PermissionResponseDto(
                                    Permission.getId(),
                                    Permission.getPermissionName(),
                                    Permission.getDescription()))
                    .collect(Collectors.toList());
        } else {
            return permissionRepo.findAll()
                    .stream()
                    .map(
                            Permission -> new PermissionResponseDto(
                                    Permission.getId(),
                                    Permission.getPermissionName(),
                                    Permission.getDescription()))
                    .collect(Collectors.toList());
        }
    }


    @Transactional
    public UpdatePermssionResponseDto updatePermission(String roleName, List<Long> replacedTo, List<Long> replacedWith) {

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("role does not exist"));

        Set<RolesDefaultPermissions> validateNewPermission = replacedWith.stream()
                .map(p -> permissionRepo.findById(p).orElseThrow(() -> new IllegalArgumentException("permission does not exist")))
                .map(permission -> RolesDefaultPermissions.builder()
                        .role(role)
                        .permission(permission)
                        .isDeleted(false)
                        .build())
                .collect(Collectors.toSet());

        Set<RolesDefaultPermissions> validateOldPermission = role.getRolesDefaultPermissions().stream()
                .filter(rolePerm -> replacedTo.contains(rolePerm.getPermission().getId()))
                .collect(Collectors.toSet());


        role.getRolesDefaultPermissions().removeAll(validateOldPermission);
        role.getRolesDefaultPermissions().addAll(validateNewPermission);

        List<Long> updatedPermissionIds = role.getRolesDefaultPermissions().stream()
                .map(p -> p.getPermission().getId())
                .collect(Collectors.toList());

        return new UpdatePermssionResponseDto(roleName, updatedPermissionIds);
    }


    public SetPermissionDto setPermissionSubUser(User user, Long roleId, List<Long> permissionId) {

        List<User> subUsers = userRepo.getUserByParentId(user.getId());

        if (permissionId == null) {
            throw new IllegalArgumentException("Permission ID list must not be null");
        }

        Set<Long> uniqueIds = new HashSet<>(permissionId);

        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);

        List<User> subUsersToUpdate = subUsers.stream()
                .filter(u -> u.getRole().getId() == roleId)
                .collect(Collectors.toList());

        if (subUsersToUpdate.isEmpty()) {
            throw new RuntimeException("No sub-users found with Role ID: " + roleId);
        }

        for (User subUser : subUsersToUpdate) {
            subUser.getUsersPermissions().clear();
            Set<UsersPermissions> usersPermissions = permissions.stream()
                    .map(p -> UsersPermissions.builder()
                            .user(subUser)
                            .permission(p)
                            .isDeleted(false)
                            .build())
                    .collect(Collectors.toSet());

            subUser.getUsersPermissions().addAll(usersPermissions);
        }

        userRepo.saveAll(subUsersToUpdate);

        return new SetPermissionDto(roleId, permissionId);

    }

    public List<PermissionResponseDto> subUsersPermission(User user, PermissionRequestDto permissionRequestDto) {

        List<User> subUser = userRepo.findOneSubUserPerRole(user.getId());

        User targetSubUser = subUser.stream().filter(u -> u.getRole().getId() == permissionRequestDto.getId()).findFirst().orElseThrow(() -> new RuntimeException("No subUser found"));


        return targetSubUser.getRole().getRolesDefaultPermissions().stream().map(p -> new PermissionResponseDto(
                p.getPermission().getId(),
                p.getPermission().getPermissionName(),
                p.getPermission().getDescription())).collect(Collectors.toList());


    }




}
