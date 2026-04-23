package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.AccountDetailsDto;
import com.arits.expense_trancker.entity.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {


    boolean existsByUserIdAndAccountNumber(Long id, String accountNumber);


    boolean existsByPaymentMethodAndId(Long paymentMethod, Long accountId);


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
            left join provider p on ad.provider_id = p.id
            left join balance b on b.account_id = a.id
            
            where a.user_id=:userId
            and a.is_deleted = false
            """, nativeQuery = true)
    List<AccountDetailsDto> findByUserId(@Param("userId") Long userId);







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
            left join provider p on ad.provider_id = p.id
            left join balance b on b.account_id = a.id
            
            where a.user_id=:userId
            and a.payment_method_id=:paymentMethodId 
            and a.is_deleted = false
            """, nativeQuery = true)
    List<AccountDetailsDto> findByuserIdAndMethodId(@Param("userId") Long userId,
                                                    @Param("paymentMethodId") Long paymentMethodId);






    @Transactional
    @Query(value = """
            select 
                a.id,
                a.deleted_at,
                ad.account_number,
                pm.name,
                p.name,
                at.name,
                b.amount
            from account a
            join account_details ad on ad.account_id = a.id
            join account_type at on at.id = a.account_type_id
            join payment_method pm on pm.id = a.payment_method_id
            join provider p on p.id = ad.provider_id
            join currency c on c.id  = a.currency_id
            join balance b on b.account_id = a.id
            where a.user_id = :userId
            and a.is_deleted = true
            """, nativeQuery = true)
    List<Object[]> findSoftDeletedListByUserId(@Param("userId") Long userId);



    boolean existsByIdAndIsDeleted(Long accountId,
                                   boolean b);



    boolean existsByPaymentMethodAndIdAndIsDeleted(Long paymentMethod,
                                                   Long accountId,
                                                   boolean b);





    @Modifying
    @Query(value = """
            update account
            set is_deleted = false , deleted_at= null
            where id=:accountId and is_deleted =true;
            """, nativeQuery = true)
    void reviveAccount(@Param("accountId") Long accountId);





    boolean existsByIdUserIdAndIdAndIsDeleted(Long targetUserId,
                                              Long accountId,
                                              boolean status);




    @Query(value = """
            select * 
            from account 
            where id=:accountId  
                and user_id =:userId
                and is_deleted=:status;
            """,nativeQuery = true)
    Optional<Account> findByUserIdAndIdAndIsDeleted(@Param("userId") Long userId,
                                                    @Param("accountId") Long accountId,
                                                    @Param("status")boolean status);




    @Modifying
    @Query(value = """
                    delete
                    from account
                    where account_id=:accountId 
                    and is_deleted= true
                    """
            , nativeQuery = true)
    void hardDeleteById(@Param("accountId") Long accountId);





    Optional<Account> findByUserIdAndId(@Param("userId") Long userId,
                                        @Param("accountId") Long accountId);





    @Query(value = """
            select
                sum(b.amount) as totalAmount,
                count(a.id) as totalAccount,
                c.name as currencyName
            from account a
            join balance b on b.account_id=a.id
            join currency c on c.id = a.currency_id
            where a.currency_id=:currencyId
            and a.user_id=:userId
            """,nativeQuery = true)
    Optional[] getTotalBalanceByCurrency(@Param("userId")Long userId,
                                         @Param("currencyId") Long currencyId);





    @Query(value = """
        select
            a.id,
            ad.account_number,
            ad.account_holder,
            ad.nominee_name,
            pm.name,
            p.name,
            at.name,
            b.amount,
            c.name,
            a.created_at
        from account a
        join account_details ad on ad.account_id = a.id
        join payment_method pm  on pm.id = a.payment_method_id
        join provider p         on p.id = ad.provider_id
        join account_type at    on at.id = a.account_type_id
        join balance b          on b.account_id = a.id
        join currency c         on c.id = a.currency_id
        where a.is_deleted = false
        and (
               lower(ad.account_number) like lower(concat('%', :keyword, '%'))
            or lower(ad.account_holder) like lower(concat('%', :keyword, '%'))
            or lower(ad.nominee_name)   like lower(concat('%', :keyword, '%'))
            or lower(pm.name)           like lower(concat('%', :keyword, '%'))
            or lower(p.name)            like lower(concat('%', :keyword, '%'))
            or lower(at.name)           like lower(concat('%', :keyword, '%'))
        )
        limit :size offset :offset
        """, nativeQuery = true)
    List<Object[]> searchAccountsAllDatabase(@Param("keyword") String keyword,
                                             @Param("size") int size,
                                             @Param("offset") int offset);









    @Query(value = """
        select count(a.id)
        from account a
        join account_details ad on ad.account_id = a.id
        join payment_method pm  on pm.id = a.payment_method_id
        join provider p         on p.id = ad.provider_id
        join account_type at    on at.id = a.account_type_id
        where a.is_deleted = false
        and (
               lower(ad.account_number) like lower(concat('%', :keyword, '%'))
            or lower(ad.account_holder) like lower(concat('%', :keyword, '%'))
            or lower(ad.nominee_name)   like lower(concat('%', :keyword, '%'))
            or lower(pm.name)           like lower(concat('%', :keyword, '%'))
            or lower(p.name)            like lower(concat('%', :keyword, '%'))
            or lower(at.name)           like lower(concat('%', :keyword, '%'))
        )
        """, nativeQuery = true)
    Long countSearchAccountsAllDatabase(@Param("keyword") String keyword);






    @Query(value = """
        select
            a.id,
            ad.account_number,
            ad.account_holder,
            ad.nominee_name,
            pm.name,
            p.name,
            at.name,
            b.amount,
            c.name,
            a.created_at
        from account a
        join account_details ad on ad.account_id = a.id
        join payment_method pm  on pm.id = a.payment_method_id
        join provider p         on p.id = ad.provider_id
        join account_type at    on at.id = a.account_type_id
        join balance b          on b.account_id = a.id
        join currency c         on c.id = a.currency_id
        where a.is_deleted = false
        and (
            a.user_id = :userId
            or a.user_id in (
                select id from users
                where parent_id = :userId
                and is_deleted = false
            )
        )
        and (
               lower(ad.account_number) like lower(concat('%', :keyword, '%'))
            or lower(ad.account_holder) like lower(concat('%', :keyword, '%'))
            or lower(ad.nominee_name)   like lower(concat('%', :keyword, '%'))
            or lower(pm.name)           like lower(concat('%', :keyword, '%'))
            or lower(p.name)            like lower(concat('%', :keyword, '%'))
            or lower(at.name)           like lower(concat('%', :keyword, '%'))
        )
        limit :size offset :offset
        """, nativeQuery = true)
    List<Object[]> searchAccountsByUserId(@Param("userId") Long userId,
                                          @Param("keyword") String keyword,
                                          @Param("size") int size,
                                          @Param("offset") int offset );




    @Query(value = """
        select count(a.id)
        from account a
        join account_details ad on ad.account_id = a.id
        join payment_method pm  on pm.id = a.payment_method_id
        join provider p         on p.id = ad.provider_id
        join account_type at    on at.id = a.account_type_id
        where a.is_deleted = false
        and (
            a.user_id = :userId
            or a.user_id in (
                select id from users
                where parent_id = :userId
                and is_deleted = false
            )
        )
        and (
               lower(ad.account_number) like lower(concat('%', :keyword, '%'))
            or lower(ad.account_holder) like lower(concat('%', :keyword, '%'))
            or lower(ad.nominee_name)   like lower(concat('%', :keyword, '%'))
            or lower(pm.name)           like lower(concat('%', :keyword, '%'))
            or lower(p.name)            like lower(concat('%', :keyword, '%'))
            or lower(at.name)           like lower(concat('%', :keyword, '%'))
        )
        """, nativeQuery = true)
    Long countSearchedAccountByUserId(@Param("userId") Long userId,
                                      @Param("keyword") String keyword);




}
