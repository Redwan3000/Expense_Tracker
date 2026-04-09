package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.MobileAccountsDetailsDto;
import com.arits.expense_trancker.entity.MobileBanking;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface MobileBankingRepo extends JpaRepository<MobileBanking, Long> {


    boolean existsByProviderNameAndPhoneNumber(String providerName, String providerName1);


    Optional<MobileBanking> findByUserAndId(User user, Long mobileBankingId);

    @Query(value = """
            select ma.id as id ,
            ma.provider_name as providerName , 
            ma.phone_number as phoneNumber , 
            at.name as accountType ,
            ma.balance as balance
            from mobile_banking ma 
            left join account_type at 
            on ma.account_type_id = at.id
            where ma.user_id=:id
            """, nativeQuery = true)
    List<MobileAccountsDetailsDto> findAccountDetailsByUserId(@Param("id") Long id);


    @Modifying
    @Transactional
    @Query(value = """
                        update mobile_banking 
                        set balance=case 
                        when :isIncome is true then balance+:amount 
                        when :isIncome is false then balance-:amount
                            else balance
                        end
                          where user_id=:userId 
                        and id=:accountId """,nativeQuery = true)
    void updateMobileBalance(@Param("userId") Long userId, @Param("accountId")Long accountId, @Param("amount")BigDecimal amount, @Param("isIncome")boolean isIncome);




@Transactional
    @Query(value = """
            select
            
                    concat(u.first_name, ' ',u.last_name) as accountHolder,
                    m.id as accountId,
                    at.name as accountType ,
                    m.provider_name as providerName,
                    m.phone_number as phoneNumber,
                    m.balance as balance,
                    c.name as currency
            from mobile_banking m 
                    left join account_type at on m.account_type_id = at.id
                    left join currency c on m.currency_id = c.id
                    left join users u on m.user_id = u.id
            where m.user_id=:id and m.id =:accountId
            
            
            """,nativeQuery = true)
   List<MobileAccountsDetailsDto> getAccountDetials(Long id, Long accountId);

    @Query(value = """
        SELECT id
        from mobile_banking 
            where id=:accountId 
            and user_id =:userId
        """,nativeQuery = true)
    Optional<Long> getUsersValidAccountId(@Param("userId") Long userId, @Param("accountId") Long accountId);
}
