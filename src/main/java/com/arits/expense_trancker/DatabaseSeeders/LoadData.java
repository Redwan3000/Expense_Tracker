package com.arits.expense_trancker.DatabaseSeeders;

import com.arits.expense_trancker.dto.SetPermissionDto;
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
import java.util.Set;

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
            authService.register(null, userRegisterRequestDto);
        }


        permissionService.addNewPermission("Login", "users can login");//1
        permissionService.addNewPermission("Register", "can register as owner");//2
        permissionService.addNewPermission("User Info", "users can see his or her info");//3
        permissionService.addNewPermission("Create Subuser", "users can create subUsers ");//4
        permissionService.addNewPermission("Get Any User", "where the admin or the user can filter the subusers by id or name");//5

        permissionService.addNewPermission("Subuser List", "the owner can see the subusers list");//6
        permissionService.addNewPermission("Modify Role Permission", "where the admin can add permission to the roles");//7
        permissionService.addNewPermission("Modify Subuser Permission", "where the admin can add permission to the roles");//8
        permissionService.addNewPermission("See Permission List", "where the admin can see the list of permissions");//9
        permissionService.addNewPermission("Delete Users", "Where the admin can delete any user");//10

        permissionService.addNewPermission("Get Users List", "Where the admin can see users list");//11
        permissionService.addNewPermission("Delete SubUser", "Where the user can delete any user");//12
        permissionService.addNewPermission("Get Deleted Users List", "Where the admin can see all deleted users list");//13
        permissionService.addNewPermission("See Subuser Permission List", "Where the user can see the permissions that are assigned to the subusers rolewise");//14
        permissionService.addNewPermission("Update Profile", "Where any user can update his profile");//15

        permissionService.addNewPermission("Add Expenses", "Where any user can add Expenses");//16
        permissionService.addNewPermission("See Expenses", "Where any user can See Expenses");//17
        permissionService.addNewPermission("Modify Expenses", "Where any user can Modify Expenses");//18
        permissionService.addNewPermission("Get Any User Cash Wallet", "where admin can get any user cash wallet details");//19
        permissionService.addNewPermission("Get Cash Wallet Details", "Where any user can delete his Expenses");//20


        permissionService.addNewPermission("Get Mobile Banking Account Detail", "Where any user can delete his Expenses");//21
        permissionService.addNewPermission("Get Any Mobile Banking Account Detail", "Where any user can delete his Expenses");//22
        permissionService.addNewPermission("Get Bank Account Detail", "Where any user can delete his Expenses");//23
        permissionService.addNewPermission("Get Any Bank Account Detail", "Where any user can delete his Expenses");//24


        SetPermissionDto ownerPermission = SetPermissionDto.builder()
                .roleId(1L)
                .permissionIds(Set.of(1L, 2L, 3L))
                .build();

        SetPermissionDto subownerPermission = SetPermissionDto.builder()
                .roleId(2L)
                .permissionIds(Set.of(1L, 2L, 3L))
                .build();

        SetPermissionDto childPermission = SetPermissionDto.builder()
                .roleId(3L)
                .permissionIds(Set.of(1L, 2L, 3L))
                .build();
        SetPermissionDto adminPermission = SetPermissionDto.builder()
                .roleId(4L)
                .permissionIds(Set.of(1L, 2L, 3L))
                .build();

        permissionService.setPermissionToRole(userRepo.findByUsername("ADMIN").orElseThrow(), ownerPermission);
        permissionService.setPermissionToRole(userRepo.findByUsername("OWNER").orElseThrow(), subownerPermission);
        permissionService.setPermissionToRole(userRepo.findByUsername("SUBOWNER").orElseThrow(), childPermission);
        permissionService.setPermissionToRole(userRepo.findByUsername("CHILD").orElseThrow(), adminPermission);
    }


}
