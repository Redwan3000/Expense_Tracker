package com.arits.expense_trancker.DatabaseSeeders;

import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.repository.AccountTypeRepo;
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
    private final AccountsService accountsService;


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

        accountsService.accountTypeSeeding("SAVINGS");
        accountsService.accountTypeSeeding("AGENT");
        accountsService.accountTypeSeeding("FDR");
        accountsService.accountTypeSeeding("PERSONAL");
        accountsService.accountTypeSeeding("DPS");
        accountsService.accountTypeSeeding("VIP");

        transactionService.tmSeedding("CASH");
        transactionService.tmSeedding("BANK");
        transactionService.tmSeedding("MOBILE_BANKING");

        if (userRepo.findByUsername("ADMIN").isEmpty()) {
            authService.register(null,userRegisterRequestDto);
        }


        permissionService.permissionsSeeding("Login", "users can login");//1
        permissionService.permissionsSeeding("Register", "can register as owner");//2
        permissionService.permissionsSeeding("User Info", "users can see his or her info");//3
        permissionService.permissionsSeeding("Create Subuser", "users can create subUsers ");//4
        permissionService.permissionsSeeding("Get Any User", "where the admin or the user can filter the subusers by id or name");//5
        permissionService.permissionsSeeding("Subuser List", "the owner can see the subusers list");//6
        permissionService.permissionsSeeding("Modify Role Permission", "where the admin can add permission to the roles");//7
        permissionService.permissionsSeeding("Modify Subuser Permission", "where the admin can add permission to the roles");//8
        permissionService.permissionsSeeding("See Permission List", "where the admin can see the list of permissions");//9
        permissionService.permissionsSeeding("Delete Users", "Where the admin can delete any user");//10
        permissionService.permissionsSeeding("Get Users List", "Where the admin can see users list");//11
        permissionService.permissionsSeeding("Delete SubUser", "Where the user can delete any user");//12
        permissionService.permissionsSeeding("Get Deleted Users List", "Where the admin can see all deleted users list");//13
        permissionService.permissionsSeeding("See Subuser Permission List", "Where the user can see the permissions that are assigned to the subusers rolewise");//14
        permissionService.permissionsSeeding("Update Profile", "Where any user can update his profile");//15
        permissionService.permissionsSeeding("Add Expenses", "Where any user can add Expenses");//16
        permissionService.permissionsSeeding("See Expenses", "Where any user can See Expenses");//17
        permissionService.permissionsSeeding("Modify Expenses", "Where any user can Modify Expenses");//18
        permissionService.permissionsSeeding("Get Any User Cash Wallet","where admin can get any user cash wallet details");//19
        permissionService.permissionsSeeding("Get Cash Wallet Details", "Where any user can delete his Expenses");//20

        permissionService.permissionsSeeding("Get Mobile Banking Account Detail", "Where any user can delete his Expenses");//21
        permissionService.permissionsSeeding("Get Any Mobile Banking Account Detail", "Where any user can delete his Expenses");//22

        permissionService.permissionsSeeding("Get Bank Account Detail", "Where any user can delete his Expenses");//23
        permissionService.permissionsSeeding("Get Any Bank Account Detail", "Where any user can delete his Expenses");//24

        permissionService.setPermissionToRole(1L, List.of(3L, 4L,19L,21L,21L,23L));
        permissionService.setPermissionToRole(2L, List.of(3L, 15L, 16L, 17L, 18L));
        permissionService.setPermissionToRole(3L, List.of(3L, 15L));
        permissionService.setPermissionToRole(4L, List.of(3L, 4L, 5L, 7L, 9L, 10L, 11L, 13L, 14L, 15L, 16L, 17L, 18L,20L,22L,24L));


    }


}
