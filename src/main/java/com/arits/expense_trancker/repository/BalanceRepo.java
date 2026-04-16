package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
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
            
            """,nativeQuery = true)
    BigDecimal getAllAccountBalance(@Param("userId") Long userId);
}
