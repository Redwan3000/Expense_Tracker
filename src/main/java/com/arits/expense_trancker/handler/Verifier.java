package com.arits.expense_trancker.handler;



import com.arits.expense_trancker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Verifier {


    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final AccountDetailsRepo accountDetailsRepo;
    private final AccountTypeRepo accountTypeRepo;


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


    public void checkAccountIdExistByPaymentMethod(Long paymentMethod, Long accountId) {
        if (!accountRepo.existsByPaymentMethodAndId(paymentMethod, accountId)) {
            throw new RuntimeException("Account not found with id: " + accountId);
        }
    }




}
