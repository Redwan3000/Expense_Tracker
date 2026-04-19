package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.AccountDetailsDto;
import com.arits.expense_trancker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {


    boolean existsByUserIdAndAccountNumber(Long id, String accountNumber);


    boolean existsByPaymentMethodAndId(Long paymentMethod, Long accountId);

    Optional<Account> findByUserIdAndId(Long id, Long accountId);


    @Query(value = """
            select a.id as accountId,
            c.name as currency,
            at.name as accountType,
            pm.name as paymentMethod, 
            ad.account_number as accountNumber,
            ad.nominee_name as nomineeName,
            p.name as providerName,
            b.balance as currentBalance,
            ad.account_holder as accountHolder,
            a.created_at as createdAt
            from account a 
                left join currency c on a.currency_id = c.id
            left join account_type at on a.account_type_id = at.id
            left join payment_method pm on a.payment_method_id = pm.id
            left join account_details ad on a.account_details = ad.id
            left join provider_list p on ad.provider_id = p.id
            left join balance b on b.account_id = a.id
            
            where a.user_id=:userId
            and a.is_deleted = false
            """, nativeQuery = true)
    List<AccountDetailsDto> findByUserId(@Param("userId") Long userId);


    Long userId(Long userId);
}
