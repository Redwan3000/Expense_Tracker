package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.BankAccountsDetailDto;
import com.arits.expense_trancker.entity.Bank;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<Bank,Long>{



    Optional<Bank> findByUserAndId(User user, long id);

    boolean existsByUserIdAndAccountNumber(Long id, @NotBlank(message = "Account number cannot be null") String accountNumber);



    @Query(value = """
            select ba.id as id ,
            ba.bank_name as bankName , 
            ba.account_number as accountNumber , 
            at.name as accountType ,
            ba.balance as balance,
            c.name as currency
            from bank ba 
            left join account_type at on ba.account_type_id = at.id 
            left join currency c on ba.currency_id=c.id
            where ba.user_id=:id
            """, nativeQuery = true)
    List<BankAccountsDetailDto> findAccountDetailsByUserId(@Param("id") Long id);


@Modifying
@Transactional
    @Query(value = """
                        update bank 
                        set balance=case 
                        when :isIncome is true then balance+:amount 
                        when :isIncome is false then balance-:amount
                            else balance
                        end
                          where user_id=:userId 
                        and id=:accountId """,nativeQuery = true)
    void updateBankBalance(@Param("userId") Long userId, @Param("accountId")Long accountId, @Param("amount")BigDecimal amount, @Param("isIncome")boolean isIncome);


@Transactional
@Query(value = """
        select
                b.id as id,
                b.bank_name as bankName,
                b.account_number as accountNumber,
                at.name as accountType ,
                b.balance as balance,
                c.name as currency,
                concat(u.first_name, ' ',u.last_name) as accountHolder
        from bank b 
                left join account_type at on b.account_type_id = at.id
                left join currency c on b.currency_id = c.id
                left join users u on b.user_id = u.id
        where b.user_id=:id 
            and (:accountId<=0 or b.id =:accountId)
                         
        
        """,nativeQuery = true)
    List<BankAccountsDetailDto> getAccountDetails(@Param("id") Long id, @Param("accountId") Long accountId);


@Query(value = """
        select id 
        from bank 
        where id=:accountId and user_id=:personId
        """,nativeQuery = true)
    Optional<Long> getUsersValidAccountId(@Param("personId") Long personId,@Param("accountId") Long accountId);
}