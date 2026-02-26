package com.arits.expense_trancker.DatabaseSeeders;


import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.entity.Gender;
import com.arits.expense_trancker.entity.Permission;
import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.permissionRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.service.GenderService;
import com.arits.expense_trancker.service.PermissionsService;
import com.arits.expense_trancker.service.RoleService;
import com.arits.expense_trancker.service.UserService;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor

public class LoadData implements CommandLineRunner {

    private final RoleService roleService;

    private final GenderService genderService;

    private final PermissionsService permissionsService;

    private final UserService userService;

    private UserRegisterRequestDto userRegisterRequestDto= UserRegisterRequestDto.builder()
            .first_name("ADMIN").last_name("ADMIN").email("admin@gmail.com").phone("123456789").password("ADMIN").dob(LocalDate.of(2001,9,5)).gender_id(1L).username("ADMIN").role_id(4L)
            .build();


    @Override
    @Transactional
    public void run(String... args) throws Exception {


        genderService.genderSeeding("Male");
        genderService.genderSeeding("Female");
        genderService.genderSeeding("Others");

        roleService.roleSeeding("OWNER");
        roleService.roleSeeding("SUBOWNER");
        roleService.roleSeeding("TERTIARY");
        roleService.roleSeeding("ADMIN");


        permissionsService.permissionsSeeding("login", "users can login");
        permissionsService.permissionsSeeding("register", "can register as owner");
        permissionsService.permissionsSeeding("user info", "users can see his or her info");
        permissionsService.permissionsSeeding("create subuser", "users can create subUsers ");
        permissionsService.permissionsSeeding("get any user", "where the admin or the user can filter the subusers by id or name");
        permissionsService.permissionsSeeding("subuser list", "the owner can see the subusers list");
        permissionsService.permissionsSeeding("modify role permission", "where the admin can add permission to the roles");
        permissionsService.permissionsSeeding("modify subuser permission", "where the admin can add permission to the roles");
        permissionsService.permissionsSeeding("see permission list", "where the admin can see the list of permissions");




        permissionsService.assigningPermissions(1l, List.of(1l, 2l, 3l, 4l,6l));
        permissionsService.assigningPermissions(2l, List.of(1l,2l,3l));
        permissionsService.assigningPermissions(3l, List.of(1l, 2l, 3l, 4l, 5l, 6l,7l,8l,9l));
        userService.defaultAdmin(userRegisterRequestDto);

    }


}
