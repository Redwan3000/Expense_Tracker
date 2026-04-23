package com.arits.expense_trancker.handler;


import com.arits.expense_trancker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Verifier {


    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final AccountDetailsRepo accountDetailsRepo;
    private final AccountTypeRepo accountTypeRepo;
    private final CurrencyRepo currencyRepo;
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final RolesDefaultPermissionsRepo rolesDefaultPermissionsRepo;
    private final UsersPermissionsRepo usersPermissionsRepo;


    public void checkUserExistence(Long userId) {
        if (userId != null && !userRepo.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    public void checkAccountExistence(Long accountId) {
        if (accountId != null && !accountRepo.existsById(accountId)) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
    }

    public void checkSoftDeletedAccountExistence(Long accountId) {
        if (accountId != null && !accountRepo.existsByIdAndIsDeleted(accountId, true)) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
    }

    public void checkAccountDetailsExistence(Long accountDetailsId) {
        if (accountDetailsId != null && !accountDetailsRepo.existsById(accountDetailsId)) {
            throw new RuntimeException("AccountDetail not found with id: " + accountDetailsId);
        }
    }

    public void checkAccountTypeExistence(Long typeId) {
        if (typeId != null && !accountTypeRepo.existsById(typeId)) {
            throw new RuntimeException("AccountType not found with id: " + typeId);
        }
    }


    public void checkPaymentMethodExistence(Long paymentMethod) {
        if (paymentMethod != null && !paymentMethodRepo.existsById(paymentMethod)) {
            throw new RuntimeException("paymentMethod not found with id: " + paymentMethod);
        }
    }

    public void checkSoftDeletedPaymentMethodExistence(Long paymentMethod) {
        if (paymentMethod != null && !paymentMethodRepo.existsByIdAndIsDeleted(paymentMethod, true)) {
            throw new RuntimeException("paymentMethod not found with id: " + paymentMethod);
        }
    }


    public void checkAccountIdExistByPaymentMethod(Long paymentMethod, Long accountId) {
        if (!accountRepo.existsByPaymentMethodAndId(paymentMethod, accountId)) {
            throw new RuntimeException("Account not found with id: " + accountId + "in this method id: " + paymentMethod);
        }
    }

    public void checkSoftDeletedAccountIdExistByPaymentMethod(Long paymentMethod, Long accountId) {
        if (!accountRepo.existsByPaymentMethodAndIdAndIsDeleted(paymentMethod, accountId, true)) {
            throw new RuntimeException("Account not found with id: " + accountId + "in this method id: " + paymentMethod);
        }
    }


    public void checkAccountIdExistByUserId(Long targetUserId, Long accountId, boolean status) {
        if (!accountRepo.existsByIdUserIdAndIdAndIsDeleted(targetUserId, accountId, status)) {
            throw new RuntimeException("account does not belong to the userId : " + targetUserId);
        }
    }

    public void checkCurrencyExistance(Long currencyId) {
        if (!currencyRepo.existsById(currencyId)) {

            throw new RuntimeException("currency does not belong to the userId : " + currencyId);
        }
    }

    public void checkAccountBalanceForTransfer(Long targetUserId, Long fromAccountId, BigDecimal transferAmount) {
    }

    public void checkRoleExistanceById(Long roleId) {
        if (!roleRepo.existsById(roleId)) {
            throw new RuntimeException("invalid Role Id");
        }
    }

    public void checkPermissionIds(Set<Long> permissionIds) {
        for (Long p : permissionIds) {
            if (!permissionRepo.existsById(p)) {
                throw new RuntimeException("Permission Does not exist by Id : " + p);
            }
        }
    }

    public void checkUserExistanceAsParentOfRole(Long parentId, Long roleId) {

        if (!userRepo.existsUserByParentIdAndRoleId(parentId, roleId)) {
            throw new RuntimeException("user does not have any subuser with the role");
        }
    }


    public void checkPermissionIdsExistsInRole(Long roleId, Set<Long> permissionIds) {

        if (rolesDefaultPermissionsRepo.countPermissionsInRole(roleId, permissionIds) != permissionIds.size()) {
            throw new RuntimeException("One or more permissions are not associated with this role.");
        }
    }

    public void checkPermissionIdsEmptyOrNot(Set<Long> permissionIds) {
        if (permissionIds.isEmpty()) {
            throw new RuntimeException("permission sets cannot be null or empty");
        }
    }

    public void checkPermissionIdsExistsInUser(Long targetUserId, Set<Long> permissionIds) {
        if (usersPermissionsRepo.countPermissionsInUser(targetUserId, permissionIds) != permissionIds.size()) {
            throw new RuntimeException("One or more permissions are not associated with this role.");
        }


    }

    public void checkIsNamesNullOrNot(String name) {
        if (name.isBlank()) {
            throw new RuntimeException("names can not be null or empty");
        }
    }

    public boolean checkpermissionExistance(String name) {
        return permissionRepo.existsByNameIncludingSofted(name);
    }

    public void checkPermissionExistanceById(Long permissionId) {
        if (!permissionRepo.existsById(permissionId)) {
            throw new RuntimeException("permission does not exist with the id : " + permissionId);
        }
    }

    public void checkPermissionExistanceInSofted(Long permissionId) {
        if(!permissionRepo.existsAfterSoftDelete(permissionId)){
            throw new RuntimeException("permission is not present as softdeleted id :"+permissionId);
        }
    }

    public void checkPermissionInBothWorld(Long permissionId) {
        if(!permissionRepo.permissionExistInBothWorld(permissionId)){
            throw new RuntimeException("permission is not present anywhere with id: "+permissionId);
        }
    }

    public void checkPermissionId(Long permissionId) {
        if(!permissionRepo.existsById(permissionId)){
            throw new RuntimeException("permission does not exist by the id : "+permissionId);
        }


    }
}
