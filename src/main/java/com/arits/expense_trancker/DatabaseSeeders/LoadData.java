package com.arits.expense_trancker.DatabaseSeeders;

import com.arits.expense_trancker.dto.UserRegisterRequestDto;
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

    private final PermissionsService permissionsService;

    private final UserService userService;

    private final TransactionService transactionService;

    private final CurrencyService currencyService;

    private UserRegisterRequestDto userRegisterRequestDto = UserRegisterRequestDto.builder()
            .first_name("ADMIN").last_name("ADMIN").email("admin@gmail.com").phone("123456789").password("ADMIN").dob(LocalDate.of(2001, 9, 5)).gender_id(1L).username("ADMIN").role_id(4L)
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


        userService.defaultAdmin(userRegisterRequestDto);

        permissionsService.permissionsSeeding("Login", "users can login");
        permissionsService.permissionsSeeding("Register", "can register as owner");
        permissionsService.permissionsSeeding("User Info", "users can see his or her info");
        permissionsService.permissionsSeeding("Create Subuser", "users can create subUsers ");
        permissionsService.permissionsSeeding("Get Any User", "where the admin or the user can filter the subusers by id or name");
        permissionsService.permissionsSeeding("Subuser List", "the owner can see the subusers list");
        permissionsService.permissionsSeeding("Modify Role Permission", "where the admin can add permission to the roles");
        permissionsService.permissionsSeeding("Modify Subuser Permission", "where the admin can add permission to the roles");
        permissionsService.permissionsSeeding("See Permission List", "where the admin can see the list of permissions");
        permissionsService.permissionsSeeding("Delete Users", "Where the admin can delete any user");
        permissionsService.permissionsSeeding("Get Users List", "Where the admin can see users list");
        permissionsService.permissionsSeeding("Delete SubUser", "Where the user can delete any user");
        permissionsService.permissionsSeeding("Get Deleted Users List", "Where the admin can see all deleted users list");
        permissionsService.permissionsSeeding("See Subuser Permission List", "Where the user can see the permissions that are assigned to the subusers rolewise");
        permissionsService.permissionsSeeding("Update Profile", "Where any user can update his profile");
        permissionsService.permissionsSeeding("Add Expenses", "Where any user can add Expenses");
        permissionsService.permissionsSeeding("See Expenses", "Where any user can See Expenses");
        permissionsService.permissionsSeeding("Modify Expenses", "Where any user can Modify Expenses");
        permissionsService.permissionsSeeding("Delete Expenses", "Where any user can delete his Expenses");

        permissionsService.assigningPermissions(1l, List.of(3l, 4l, 6l, 8l, 12l, 14l, 15l, 16l, 17l, 18l, 19l));
        permissionsService.assigningPermissions(2l, List.of(3l, 15l, 16l, 17l, 18l));
        permissionsService.assigningPermissions(3l, List.of(3l, 15l));

        permissionsService.assigningPermissions(4l, List.of(3l, 4l, 5l, 7l, 9l, 10l, 11l, 13l, 14l, 15l, 16l, 17l, 18l));


    }


}
