package com.arits.expense_trancker.DatabaseSeeders;

import com.arits.expense_trancker.dto.SetPermissionDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.AccountTypeRepo;
import com.arits.expense_trancker.repository.UserRepo;
import com.arits.expense_trancker.security.AuthService;
import com.arits.expense_trancker.service.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
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

        transactionService.tTypeSeeding("EXPENSE");
        transactionService.tTypeSeeding("INCOME");

        currencyService.currencySeeding("BDT");
        currencyService.currencySeeding("EUR");
        currencyService.currencySeeding("USD");

        accountsService.accountTypeSeeding("SAVINGS");
        accountsService.accountTypeSeeding("AGENT");
        accountsService.accountTypeSeeding("FDR");
        accountsService.accountTypeSeeding("PERSONAL");
        accountsService.accountTypeSeeding("DPS");
        accountsService.accountTypeSeeding("VIP");

        transactionService.pMethodSeeding("CASH");
        transactionService.pMethodSeeding("BANK");
        transactionService.pMethodSeeding("MOBILE_BANKING");



        if (userRepo.findByUsername("ADMIN").isEmpty()) {
            authService.register(null, null, userRegisterRequestDto);
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






//        Account Module's Permissions
        permissionService.addNewPermission("Add Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Add Account For SubUser", "users can add accounts for himself");//26
        permissionService.addNewPermission("Add Account For Any User", "users can add accounts for himself");//27
        permissionService.addNewPermission("Add Account For Any Users SubUser", "users can add accounts for himself");//28
        permissionService.addNewPermission("Modify Account Details", "users can add accounts for himself");//29
        permissionService.addNewPermission("Modify Account Details For SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Modify Account Details For Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Modify Account Details For Any Users SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete Subuser Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete Any User Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete Any Users Subuser Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Own Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Subuser Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any User Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any Users Subuser Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Own All Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Subuser All Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any User All Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any Users Subuser All Account Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Own All Account Method Wise Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Subuser All Account Method Wise Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any User All Account Method Wise Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Any Users Subuser All Account Method Wise Details", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Soft Deleted Account List", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Soft Deleted Account List of SubUsers", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Soft Deleted Account List of Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Soft Deleted Account List of Any Users SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Restore Own SoftDeleted Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Restore SubUsers SoftDeleted Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Restore Own SoftDeleted Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Restore SoftDeleted Account of Any Users", "users can add accounts for himself");//25
        permissionService.addNewPermission("Restore SoftDeleted Account of Any Users SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete Own SoftDeleted Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete SubUsers SoftDeleted Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete SoftDeleted Account of Any Users", "users can add accounts for himself");//25
        permissionService.addNewPermission("Delete SoftDeleted Account of Any Users SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Account Balance", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Account Balance of SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Account Balance of Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Account Balance of Any Users SubUsers", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Total Balance Currency Wise", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Total Balance Currency Wise of SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Total Balance Currency Wise of Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Get Total Balance Currency Wise of Any Users SubUsers", "users can add accounts for himself");//25
        permissionService.addNewPermission("Update Account Balance", "users can add accounts for himself");//25
        permissionService.addNewPermission("Update Account Balance of SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Update Account Balance of Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Update Account Balance of Any Users SubUsers", "users can add accounts for himself");//25
        permissionService.addNewPermission("Transfer Balance to Any Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Transfer Balance to Any Account of SubUser", "users can add accounts for himself");//25
        permissionService.addNewPermission("Transfer Balance to Any Account of Any User", "users can add accounts for himself");//25
        permissionService.addNewPermission("Transfer Balance to Any Account of Any Users SubUsers", "users can add accounts for himself");//25
        permissionService.addNewPermission("Search Account", "users can add accounts for himself");//25
        permissionService.addNewPermission("Search Account of All Users", "users can add accounts for himself");//25
        permissionService.addNewPermission("Search Account of All Users By UserId", "users can add accounts for himself");//25


        SetPermissionDto ownerPermission = SetPermissionDto.builder()
                .roleId(1L)
                .permissionIds(Set.of(1L, 2L, 3L,25L,26L,27L,28L,29L,30L,31L,32L,33L,34L,35L,36L,37L,38L,39L,40L,41L,42L,43L,44L,45L,46L,47L,48L,49L,50L,51L,52L,53L,54L,55L,56L,57L,58L,59L,60L,61L,62L,63L,64L,65L,66L,67L,68L,69L,70L,71L,72L,73L,74L,75L,76L,77L,78L,79L))
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
                .permissionIds(Set.of(2L, 3L,27L,28L,29L,30L,31L,32L,33L,34L,35L,36L,37L,38L,39L,40L,41L,42L,43L,44L,45L,46L,47L,48L,49L,50L,51L,52L,53L,54L,55L,56L,57L,58L,59L,60L,61L,62L,63L,64L,65L,66L,67L,68L,69L,70L,71L,72L,73L,74L,75L,76L,77L,78L,79L))
                .build();



        permissionService.setPermissionToRole(ownerPermission);
        permissionService.setPermissionToRole(subownerPermission);
        permissionService.setPermissionToRole(childPermission);
        permissionService.setPermissionToRole(adminPermission);
//        permissionService.setPermissionToRole(userRepo.findByUsername("ADMIN").orElseThrow(), subownerPermission);
//        permissionService.setPermissionToRole(userRepo.findByUsername("ADMIN").orElseThrow(), childPermission);
//        permissionService.setPermissionToRole(userRepo.findByUsername("ADMIN").orElseThrow(), adminPermission);


    }


}
