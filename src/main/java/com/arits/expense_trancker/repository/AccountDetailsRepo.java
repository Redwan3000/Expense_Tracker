package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepo extends JpaRepository<AccountDetails, Long> {

    @Modifying
    @Query(value = """
            update account_details
            set is_deleted = false , deleted_at= null
            where account_id=:accountId and is_deleted =true;
            """, nativeQuery = true)
    void reviveAccountDetialsByAccountId(@Param("accountId") Long accountId);


    @Modifying
    @Query(value = """
                    delete
                    from account_details
                    where account_id=:accountId 
                    and is_deleted= true
                    """
            , nativeQuery = true)
    void hardDeleteByAccountId(@Param("accountId") Long accountId);
}
