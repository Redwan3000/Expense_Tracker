package com.arits.expense_trancker.handler;

import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class EntityProvider {

    private final AccountTypeRepo accountTypeRepo;
    private final AccountRepo accountRepo;
    private final AccountDetailsRepo accountDetailsRepo;
    private final BalanceRepo balanceRepo;
    private final CurrencyRepo currencyRepo;
    private final GenderRepo genderRepo;
    private final PaymentMethodRepo paymentMethodRepo;
    private final PermissionRepo permissionRepo;
    private final ProviderRepo providerRepo;
    private final RoleRepo roleRepo;
    private final RolesDefaultPermissionsRepo rolesDefaultPermissionsRepo;
    private final TransactionRepo transactionRepo;
    private final TransactionTypeRepo transactionTypeRepo;
    private final UserRepo userRepo;


    public AccountType getAccountTypeById(Long id){

        return accountTypeRepo.findById(id)
                .orElseThrow(()->new RuntimeException("AccountType does not exist with the Id: "+ id));

    }

    public Account getAccountById(Long id){

        return accountRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Account does not exist with the Id: "+ id));

    }
    public AccountDetails getAccountDetailsById(Long id){

        return accountDetailsRepo.findById(id)
                .orElseThrow(()->new RuntimeException("AccountDetails does not exist with the Id: "+ id));

    }
    public Balance getBalanceById(Long id){

        return balanceRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Balance does not exist with the Id: "+ id));

    }


    public Currency getCurrencyById(Long id){

        return currencyRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Currency does not exist with the Id: "+ id));

    }
    public Gender getGenderById(Long id){

        return genderRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Gender does not exist with the Id: "+ id));
    }

    public PaymentMethod getPaymentMethodById(Long id){

        return paymentMethodRepo.findById(id)
                .orElseThrow(()->new RuntimeException("PaymentMethod does not exist with the Id: "+ id));
    }

    public Permission getPermissionById(Long id){

        return permissionRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Permission does not exist with the Id: "+ id));
    }

    public Provider getProviderById(Long id){

        return providerRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Provider does not exist with the Id: "+ id));
    }

    public Role getRoleById(Long id){

        return roleRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Role does not exist with the Id: "+ id));
    }


    public RolesDefaultPermissions getRolesDefaultPermissionById(Long id){

        return rolesDefaultPermissionsRepo.findById(id)
                .orElseThrow(()->new RuntimeException("RolesDefaultPermissions does not exist with the Id: "+ id));
    }

    public Transactions getTransactionsById(Long id){

        return transactionRepo.findById(id)
                .orElseThrow(()->new RuntimeException("Transactions does not exist with the Id: "+ id));
    }


    public TransactionType getTransactionTypeById(Long id){

        return transactionTypeRepo.findById(id)
                .orElseThrow(()->new RuntimeException("TransactionType does not exist with the Id: "+ id));
    }


    public User getUserById(Long id){
        return userRepo.findById(id)
                .orElseThrow(()->new RuntimeException("user does not exist with the Id: "+id));
    }








    public String resolveString(String dto,String existing ){


        return dto!=null?dto:existing;
    }

    public BigDecimal resolveBalance(BigDecimal dto, BigDecimal existing ){

        return dto!=null?dto:existing;
    }
    public Long resolveLong(Long dto, Long existing) {
        return dto != null ? dto : existing;
    }


    public AccountType resolveAccountType(Long id, AccountType existing){

        return id!=null ? accountTypeRepo.findById(id)
                          .orElseThrow(()->new RuntimeException("invalid AccountType"))
                :existing;
    }

    public Currency resolveCurrency(Long id, Currency existing){

        return id!=null ? currencyRepo.findById(id)
                          .orElseThrow(()->new RuntimeException("invalid AccountType"))
                :existing;
    }


    public Long getUserIdByParentIdAndUserId(Long parentId, Long userId) {
        return userRepo.findUserIdByParentIdAndUserId(parentId,userId).orElseThrow(()->new RuntimeException("subUser does not exist"));
    }
}
