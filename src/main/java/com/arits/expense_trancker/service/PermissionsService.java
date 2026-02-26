package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.dto.SetPermissionDto;
import com.arits.expense_trancker.dto.UpdatePermssionResponseDto;
import com.arits.expense_trancker.entity.Permission;
import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.permissionRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionsService {


    public final permissionRepo permissionRepo;
    public final roleRepo roleRepo;
    public final userRepo userRepo;

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
    public SetPermissionDto assigningPermissions(Long roleId, List<Long> permissionId) {

        if (permissionId == null) {
            throw new IllegalArgumentException("Permission ID list must not be null");
        }

        Set<Long> uniqueIds = new HashSet<>(permissionId);

        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found ID: " + roleId));


        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);

        role.getPermission().clear();
        role.getPermission().addAll(permissions);
        roleRepo.saveAndFlush(role);


        List<User> allUser = userRepo.findUsersByRole(role);
        Set<Permission> uniquePermissionSet = new HashSet<>(permissions);

        for (User user : allUser) {
            user.getPermissions().clear();
            user.getPermissions().addAll(uniquePermissionSet);
        }

        userRepo.saveAll(allUser);

        List<Long> permissionList = role.getPermission().stream().map(Permission::getPermissionId).toList();

        return new SetPermissionDto(role.getRoleId(), permissionList);
    }


//    @Transactional
//    public SetPermissionDto assigningPermissionsToUser(Long id,String roleName, List<Long> permissionId) {
//
//
//        if (permissionId == null) {
//            throw new IllegalArgumentException("Permission ID list must not be null");
//        }
//
//        if (id == null) {
//            throw new IllegalArgumentException("user id must nto be null");
//        }
//
//        Set<Long> uniqueIds = new HashSet<>(permissionId);
//
//        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);
//
//
//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("user not found: " + id));
//
//
//        user.getPermissions().addAll(permissions);
//        userRepo.saveAndFlush(user);
//
//        List<Long>permissionList=user.getPermissions().stream().map(Permission::getPermissionId).toList();
//
//
//
//        return new SetPermissionDto(user.getUser_id(),user.getRole().getRoleName(), permissionList);
//    }


//    @Transactional
//    public SetPermissionDto deletePermissionFromRole(String roleName, List<Long> permissionId) {
//
//
//        Set<Long> uniqueIds = new HashSet<>(permissionId);
//
//        Role role = roleRepo.findByRoleName(roleName)
//                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
//
//
//        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);
//
//
//        role.getPermission().removeAll(permissions);
//        roleRepo.saveAndFlush(role);
//
//
//        List<User> allUser = userRepo.findUsersByRole(role);
//        Set<Permission> uniquePermissionSet = new HashSet<>(permissions);
//
//        for (User user : allUser) {
//            user.getPermissions().removeAll(uniquePermissionSet);
//        }
//
//        userRepo.saveAll(allUser);
//
//        List<Long>permissionList=role.getPermission().stream().map(Permission::getPermissionId).toList();
//
//        return new SetPermissionDto(roleName, permissionList);
//    }


    public List<PermissionResponseDto> permissionList(Long roleId) {

        List<Permission> permissionsByRole = permissionRepo.findByRole_RoleId(roleId);

        if (!permissionsByRole.isEmpty()) {

            return permissionRepo.findByRole_RoleId(roleId)
                    .stream()
                    .map(
                            Permission -> new PermissionResponseDto(
                                    Permission.getPermissionId(),
                                    Permission.getPermissionName(),
                                    Permission.getDescription()))
                    .collect(Collectors.toList());
        } else {
            return permissionRepo.findAll()
                    .stream()
                    .map(
                            Permission -> new PermissionResponseDto(
                                    Permission.getPermissionId(),
                                    Permission.getPermissionName(),
                                    Permission.getDescription()))
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    public UpdatePermssionResponseDto updatePermission(String roleName, List<Long> replacedTo, List<Long> replacedWith) {

        if (!roleRepo.findByRoleName(roleName).isPresent()) {
            throw new IllegalArgumentException("role does not exist");
        }
        List<Permission> validateNewPermission = replacedWith.stream().map(
                p -> permissionRepo.findById(p).orElseThrow(() -> new IllegalArgumentException("permission does not exist"))).collect(Collectors.toList());

        List<Permission> validateOldPermission = replacedTo.stream().map(p -> permissionRepo.findById(p).orElseThrow(() -> new IllegalArgumentException("permission does not exist"))).collect(Collectors.toList());

        Role role = roleRepo.findByRoleName(roleName).orElseThrow();
        role.getPermission().removeAll(validateOldPermission);
        role.getPermission().addAll(validateNewPermission);

        List<Long> updatedPermissionIds = role.getPermission().stream()
                .map(p -> p.getPermissionId())
                .collect(Collectors.toList());
        return new UpdatePermssionResponseDto(roleName, updatedPermissionIds);
    }

    public SetPermissionDto setPermissionSubUser(User user, Long roleId, List<Long> permissionId) {


        List<User> subUsers = userRepo.getUserByParentId(user);
        if (permissionId == null) {
            throw new IllegalArgumentException("Permission ID list must not be null");
        }

        Set<Long> uniqueIds = new HashSet<>(permissionId);

        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);

        List<User> subUsersToUpdate = subUsers.stream()
                .filter(u -> u.getRole().getRoleId() == roleId)
                .collect(Collectors.toList());

        if (subUsersToUpdate.isEmpty()) {
            throw new RuntimeException("No sub-users found with Role ID: " + roleId);
        }

        for (User subUser : subUsersToUpdate) {
            subUser.getPermissions().clear();
            subUser.getPermissions().addAll(permissions);
        }

        userRepo.saveAll(subUsersToUpdate);

        return new SetPermissionDto(roleId, permissionId);

    }

//
//    @Transactional
//    public SetPermissionDto deletePermissionFromUser(Long id, List<Long> permissionId) {
//
//        Set<Long> uniqueIds = new HashSet<>(permissionId);
//
//        User user = userRepo.findById(id)
//                .orElseThrow(() -> new RuntimeException("user not found: " + id));
//
//
//        List<Permission> permissions = permissionRepo.findAllById(uniqueIds);
//
//
//        user.getPermissions().removeAll(permissions);
//        userRepo.saveAndFlush(user);
//
//
//
//        List<Long>permissionList=user.getPermissions().stream().map(Permission::getPermissionId).toList();
//
//        return new SetPermissionDto(user.getUser_id(), user.getRole().getRoleName(), permissionList);
//
//
//    }


}
