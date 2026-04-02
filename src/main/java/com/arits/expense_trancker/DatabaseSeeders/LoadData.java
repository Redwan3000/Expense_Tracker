package com.arits.expense_trancker.DatabaseSeeders;

import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.repository.UserRepo;
import com.arits.expense_trancker.security.AuthService;
import com.arits.expense_trancker.service.*;
import jakarta.transaction.Transactional;
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

    private final PermissionService permissionService;


    private final TransactionService transactionService;

    private final CurrencyService currencyService;
    private final AuthService authService;
    private final UserRepo userRepo;

    private final UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto.builder()
            .firstName("ADMIN")
            .lastName("ADMIN")
            .email("admin@gmail.com")
            .phone("123456789")
            .password("ADMIN")
            .dob(LocalDate.of(2001, 9, 5))
            .genderId(1L)
            .roleId(4L)
            .username("ADMIN")
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

        transactionService.ttSeeding("EXPENSE");
        transactionService.ttSeeding("INCOME");

        currencyService.currencySeeding("BDT");
        currencyService.currencySeeding("EUR");
        currencyService.currencySeeding("USD");


        transactionService.tmSeedding("CASH");
        transactionService.tmSeedding("BANK");
        transactionService.tmSeedding("MOBILE_BANKING");

        if (userRepo.findByUsername("ADMIN").isEmpty()) {
            authService.register(userRegisterRequestDto, null);
        }
        permissionService.permissionsSeeding("Login", "users can login");
        permissionService.permissionsSeeding("Register", "can register as owner");
        permissionService.permissionsSeeding("User Info", "users can see his or her info");
        permissionService.permissionsSeeding("Create Subuser", "users can create subUsers ");
        permissionService.permissionsSeeding("Get Any User", "where the admin or the user can filter the subusers by id or name");
        permissionService.permissionsSeeding("Subuser List", "the owner can see the subusers list");
        permissionService.permissionsSeeding("Modify Role Permission", "where the admin can add permission to the roles");
        permissionService.permissionsSeeding("Modify Subuser Permission", "where the admin can add permission to the roles");
        permissionService.permissionsSeeding("See Permission List", "where the admin can see the list of permissions");
        permissionService.permissionsSeeding("Delete Users", "Where the admin can delete any user");
        permissionService.permissionsSeeding("Get Users List", "Where the admin can see users list");
        permissionService.permissionsSeeding("Delete SubUser", "Where the user can delete any user");
        permissionService.permissionsSeeding("Get Deleted Users List", "Where the admin can see all deleted users list");
        permissionService.permissionsSeeding("See Subuser Permission List", "Where the user can see the permissions that are assigned to the subusers rolewise");
        permissionService.permissionsSeeding("Update Profile", "Where any user can update his profile");
        permissionService.permissionsSeeding("Add Expenses", "Where any user can add Expenses");
        permissionService.permissionsSeeding("See Expenses", "Where any user can See Expenses");
        permissionService.permissionsSeeding("Modify Expenses", "Where any user can Modify Expenses");
        permissionService.permissionsSeeding("Delete Expenses", "Where any user can delete his Expenses");

        permissionService.assigningPermissions(1L, List.of(3L, 4L));
        permissionService.assigningPermissions(2L, List.of(3L, 15L, 16L, 17L, 18L));
        permissionService.assigningPermissions(3L, List.of(3L, 15L));
        permissionService.assigningPermissions(4L, List.of(3L, 4L, 5L, 7L, 9L, 10L, 11L, 13L, 14L, 15L, 16L, 17L, 18L));


    }


}
