package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.AccountBalanceResponseDto;
import com.arits.expense_trancker.entity.TransactionMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepo extends JpaRepository<TransactionMethod, Long> {



@Query(value = "select * from payment_method where name=:name",nativeQuery = true)
    Optional<TransactionMethod> findByMethodName(@Param("name") String name);

    @Query(value = "select * from payment_method where id=:tm_id",nativeQuery = true)
    Optional<TransactionMethod> findByMethodId(@Param("tm_id") long tm_id);

    Optional<TransactionMethod> findByNameIgnoreCase(String name);




    @Query(value = """
                        select distinct coalesce(b.id,m.id,c.id) as id,
                               p.name as paymentMethod ,
                               coalesce(b.balance,m.balance,c.balance) as balance,
                               coalesce(bc.name, mc.name,cc.name) as currency
                        from payment_method p
                        left join cash_wallet c on p.id = c.payment_method_id 
                        left join bank b on p.id=b.payment_method_id  
                        left join mobile_banking m on p.id=m.payment_method_id  
                        left join currency bc on b.currency_id = bc.id 
                        left join currency mc on m.currency_id = mc.id 
                        left join currency cc on c.currency_id = cc.id 
                        where (c.user_id=:id or b.user_id=:id or m.user_id=:id) 
                          and p.is_deleted is false 
                          and (b.is_deleted is false or 
                               m.is_deleted is  false or 
                               c.is_deleted is false ) 
            """,nativeQuery = true)
    List<AccountBalanceResponseDto> getAllAccountBalance(@Param("id") Long id);




}
