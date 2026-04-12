package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.CashWalletDetailsDto;
import com.arits.expense_trancker.entity.CashWalletDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CashWalletRepo extends JpaRepository<CashWalletDetails, Long> {



    boolean existsByUserId(Long id);


//    Query to get the users CashWallet Details through user's id
@Transactional
    @Query(value = """
            select 
                cw.id as walletId, 
                concat(u.first_name,' ',u.last_name) as accountHolder,
                cw.balance as balance,
                c.name as currency,
                pm.name as paymentMethod
            from cash_wallet cw 
                left join users u on cw.user_id = u.id 
                left join currency c on cw.currency_id=c.id 
                left join payment_method pm on cw.payment_method_id = pm.id 
            where cw.user_id=:userId """,nativeQuery = true)
    Optional<CashWalletDetailsDto> getCashWalletDetails(@Param("userId") Long userId);





    Optional<CashWalletDetails> findByUserId(Long id);


    @Modifying
    @Transactional
    @Query(value = """
                        update cash_wallet 
                        set balance=case 
                        when :isIncome is true then balance+:amount 
                        when :isIncome is false then balance-:amount
                            else balance
                        end
                          where user_id=:userId """,nativeQuery = true)
    void updateCashBalance(@Param("userId") Long userId, @Param("amount")BigDecimal amount, @Param("isIncome")boolean isIncome);


    @Query(value = """
            select 
                id
            from cash_wallet 
            where id= :id
            """,nativeQuery = true)
    Optional<Long> findValidWalletId(@Param("id") Long id);
}
