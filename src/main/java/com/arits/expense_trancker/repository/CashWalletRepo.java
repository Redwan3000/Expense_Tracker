package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.CashAccountsBalanceDto;
import com.arits.expense_trancker.dto.CreateCashAccountResponseDto;
import com.arits.expense_trancker.dto.ModifyCashWalletDetailsDto;
import com.arits.expense_trancker.entity.CashWallet;
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
public interface CashWalletRepo extends JpaRepository<CashWallet, Long> {


    Optional<CashWallet> findByUser(User user);

    boolean existsByUserId(Long id);

    @Query(value = """
            select cw.id as id, 
                   cw.balance as balance,
                   c.name as currency
            from cash_wallet cw 
            left join currency c on cw.currency_id=c.id 
            where cw.user_id=:id
            """,nativeQuery = true)
    List<CashAccountsBalanceDto> findAccountDetailsByUserId(@Param("id") Long id);


    Optional<CashWallet> findByUserId(Long id);
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

}
