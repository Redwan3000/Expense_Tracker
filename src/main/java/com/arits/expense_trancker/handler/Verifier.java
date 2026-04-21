package com.arits.expense_trancker.handler;


import com.arits.expense_trancker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
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
            if (permissionRepo.existsById(p)) {
                throw new RuntimeException("Permission Does not exist by Id : " + p);
            }
        }
    }
}
