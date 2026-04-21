package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Balance;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface BalanceRepo extends JpaRepository<Balance, Long> {


    @Query(value = """
            
            select coalesce(sum(b.balance),0)
            from balance b 
            left join account a on b.account_id= a.id
            where a.user_id=:userId
            
            """, nativeQuery = true)
    BigDecimal getAllAccountBalance(@Param("userId") Long userId);

    @Modifying
    @Query(value = """
            update balance
            set is_deleted = false , deleted_at= null
            where account_id=:accountId and is_deleted =true;
            """, nativeQuery = true)
    void reviveBalanceByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Query(value = """
            delete
            from balance
            where account_id=:accountId 
            and is_deleted= true
            """
            , nativeQuery = true)
    void hardDeleteByAccountId(@Param("accountId") Long accountId);

    @Modifying
    @Query(value = """
            update balance
            set amount=:amount
            where account_id=:accountId
            """, nativeQuery = true)
    void updateAccountBalance(@Param("accountId") Long accountId, @Param("amount") BigDecimal amount);


    @Modifying
    @Query(value = """
    UPDATE balance
    SET amount = amount - :amount
    FROM account a
    WHERE a.id = :accountId
    AND a.user_id = :userId
    AND balance.account_id = :accountId
    AND balance.amount >= :amount
    """, nativeQuery = true)
    void debitBalance(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );

    @Modifying
    @Query(value = """
    UPDATE balance
    SET amount = amount + :amount
    FROM account a
    WHERE a.id = :accountId
    AND a.user_id = :userId
    AND balance.account_id = :accountId
    """, nativeQuery = true)
    void creditBalance(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );
}
