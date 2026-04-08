package com.arits.expense_trancker.repository;
import com.arits.expense_trancker.entity.PaymentMethod;
import com.arits.expense_trancker.entity.Transactions;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions,Long> {



    @Query(value = """
            select t.* 
            from transactions t 
            join users u  on t.user_id = u.id 
            where (u.id=:userId or u.parent_id=:userId)
                        and (:itemName is null or t.item_name=:itemName)
                        and(:username is null or u.username=:username)
                        and(cast(:singleDate as date) is null or t.date = cast(:singleDate as date))
                        and(cast(:fromDate as date) is null or t.date >= cast(:fromDate as date))
                        and(cast(:toDate as date) is null or t.date <= cast(:toDate as date))
            """,nativeQuery = true)
    List<Transactions> findTransactionsOfOwnerAndSubowner(@Param("userId") Long userId,@Param("username") String username,
                                                          @Param("itemName") String itemName,
                                                          @Param("singleDate") LocalDate singleDate,
                                                          @Param("fromDate") LocalDate fromDate,
                                                          @Param("toDate") LocalDate toDate);




@Query(value = "select * from transactions where user_id=:userId and is_deleted=true and id=:tId",nativeQuery = true)
    Optional<Transactions> findDeletedTransactionsByUserId(@Param("userId") Long userId ,@Param("tId") Long tId);

    Optional<Transactions> findByUserAndId(User user, long id);



    @Modifying
    @Transactional
    @Query(value = "update transactions set is_deleted = true, deleted_at = NOW() where id = :transaction_id", nativeQuery = true)
    void softDeleteTransactions(@Param("transaction_id") Long transaction_id);


    List<Transactions> findByPaymentMethodAndAccountId(PaymentMethod transactionMethodNotFound, long id);
}
