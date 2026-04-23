package com.arits.expense_trancker.service;

import com.arits.expense_trancker.Mapper.PermissionMapper;
import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.handler.Resolver;
import com.arits.expense_trancker.handler.Verifier;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private final PermissionMapper permissionMapper;


    //service for setting new sets of permissions to the role
    @Transactional
    public SetPermissionDto setPermissionToRole(User user, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.softDeleteUnwantedPermissions(dto.getRoleId(), dto.getPermissionIds());
            rolesDefaultPermissionsRepo.setNewPermissions(dto.getRoleId(), permissionArray);
        } else {
            userID = user.getId();
            verifier.checkPermissionIdsExistsInRole(dto.getRoleId(), dto.getPermissionIds());
        }

        usersPermissionsRepo.softDeleteUnwantedUsersPermissions(dto.getRoleId(), permissionArray, userID);
        usersPermissionsRepo.setNewRolePermissionsToUsers(dto.getRoleId(), permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(dto.getRoleId())
                .permissionIds(dto.getPermissionIds())
                .build();

    }


    //    service for adding a new permission to the roles
    @Transactional
    public SetPermissionDto addNewPermissionToRole(User user, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.setNewPermissions(dto.getRoleId(), permissionArray);
        } else {
            userID = user.getId();

        }

        usersPermissionsRepo.setNewRolePermissionsToUsers(dto.getRoleId(), permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(dto.getRoleId())
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


    //service for removing permissions from role
    @Transactional
    public SetPermissionDto removePermissionFromRole(User user, SetPermissionDto dto) {

        Long userID;
        boolean isHeAdmin = user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Long[] permissionArray = dto.getPermissionIds().toArray(new Long[0]);


        if (isHeAdmin) {
            userID = null;
            rolesDefaultPermissionsRepo.softDeleteUnwantedPermissions(dto.getRoleId(), dto.getPermissionIds());
        } else {
            userID = user.getId();
        }
        usersPermissionsRepo.softDeleteUnwantedUsersPermissions(dto.getRoleId(), permissionArray, userID);

        return SetPermissionDto.builder()
                .roleId(dto.getRoleId())
                .permissionIds(dto.getPermissionIds())
                .build();

    }


    //service to remove Permission From User
    public SetPermissionDto removePermissionFromUser(User user, Long userId, Long subuserId, SetPermissionDto dto) {

        Long targetUserid = resolver.getTargetUserId(user, userId, subuserId);
        usersPermissionsRepo.softDeleteUsersPermissions(targetUserid, dto.getPermissionIds());

        return SetPermissionDto.builder()
                .permissionIds(dto.getPermissionIds())
                .build();

    }


    //service to add new permissions to the system
    @Transactional
    public PermissionResponseDto addNewPermission(String name, String des) {

        return permissionMapper.toAddNewPermissionDto(permissionRepo.findByNameIncludingSoft(name)
                .orElseGet(() -> permissionRepo.save(
                        Permission.builder()
                                .name(name)
                                .description(des)
                                .build())
                ));

    }

    //    service to modify a permission
    public PermissionResponseDto modifyPermission(Long permissionId, AddNewPermissionRequestDto dto) {

        return permissionMapper.toAddNewPermissionDto(
                permissionRepo.modifyPermission(
                        permissionId,
                        dto.getName(),
                        dto.getDescription()));
    }


    //    service to softDelete delete a permission
    public HardDeletePermissionResponse softDeletePermission(Long permissionId) {

        permissionMapper.toDeletePermissionDto(permissionRepo.deletePermission(permissionId));
        return HardDeletePermissionResponse.builder()
                .id(permissionId)
                .status("SOFT_DELETED")
                .message("Permission Moved to Trash")
                .build();
    }

//service to hard delete permission
    public HardDeletePermissionResponse hardDeletePermission(User user,Long permissionId) {

        if(user.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_SUPER_ADMIN"))){
            permissionRepo.hardDeletePermission(permissionId);
        }else {
            throw new RuntimeException("Only Super Admin Can permanetly Delete");
        }

        return HardDeletePermissionResponse.builder()
                .id(permissionId)
                .status("PERMANENTLY_DELETED")
                .message("The permission were removed from the system")
                .build();
    }



    //service to hard delete every permission  (the doomsday Button for permissions)
    public HardDeletePermissionResponse hardDeleteAllPermission(User user) {
        if(user.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_SUPER_ADMIN"))){
            permissionRepo.hardDeleteAllPermissions();
        }else {
            throw new RuntimeException("Only Super Admin Can permanetly Delete");
        }

        return HardDeletePermissionResponse.builder()
                .id(null)
                .status("PERMANENTLY_DELETED")
                .message("All The Permissions Were Removed From The System")
                .build();
    }


    public PaginatedResponseDto<PermissionResponseDto> searchPermission(String keyword, Long userId, Long roleId, Long permissionId,boolean isDeleted, int page, int size) {


        int offset = page * size;
        List<Object[]> searchedPermission=permissionRepo.searchPermission(keyword,userId ,roleId,permissionId,isDeleted,size,offset);
        Long totalPermission= permissionRepo.countsearchedPermission(keyword,userId ,roleId,permissionId,isDeleted);


        int totalPages =(int)Math.ceil((double)totalPermission/ size);

        List<PermissionResponseDto> content = searchedPermission.stream()
                .map(row -> PermissionResponseDto.builder()
                        .id((Long) row[0])
                        .name((String)row[1])
                        .description((String)row[2])
                        .createdAt(((Timestamp)row[5]).toLocalDateTime())
                        .build())
                .toList();
        return PaginatedResponseDto.<PermissionResponseDto>builder()
                .content(content)
                .currentPage(page)
                .pageSize(size)
                .totalElements(totalPermission)
                .totalPages(totalPages)
                .isFirst(page == 0)
                .isLast(page >= totalPages - 1)
                .build();
    }



}

