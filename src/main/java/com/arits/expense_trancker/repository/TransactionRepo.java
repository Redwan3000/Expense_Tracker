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

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions,Long> {


    @Query("select t from Transactions t " +
            "JOIN fetch t.user u join fetch t.transactionType tt join fetch t.paymentMethod tm " +
            "where u.id=:userId or u.parent.id=:userId order by t.date DESC ")
    List<Transactions> findTransactionsByUserIDAndParentID(@Param("userId") Long userId);

@Query(value = "select * from transactions where user_id=:userId and is_deleted=true and id=:tId",nativeQuery = true)
    Optional<Transactions> findDeletedTransactionsByUserId(@Param("userId") Long userId ,@Param("tId") Long tId);

    Optional<Transactions> findByUserAndId(User user, long id);



    @Modifying
    @Transactional
    @Query(value = "update transactions set is_deleted = true, deleted_at = NOW() where id = :transaction_id", nativeQuery = true)
    void softDeleteTransactions(@Param("transaction_id") Long transaction_id);


    List<Transactions> findByPaymentMethodAndAccountId(PaymentMethod transactionMethodNotFound, long id);
}
