package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transactions,Long> {


    @Query("select t from Transactions t " +
            "JOIN fetch t.user u join fetch t.transactionType tt join fetch t.transactionMethods tm " +
            "where u.userId=:userId or u.parent.userId=:userId order by t.date DESC ")
    List<Transactions> findTransactionsByUserIDAndParentID(@Param("userId") Long userId);

    @Query("SELECT t FROM Transactions t WHERE t.user.userId = :userId AND t.transactionId = :tId")
    Optional<Transactions> findByUserIdAndTransactionId(@Param("userId") Long userId,@Param("tId") Long tId);
}
