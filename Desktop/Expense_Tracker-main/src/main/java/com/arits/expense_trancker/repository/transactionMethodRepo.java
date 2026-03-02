package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.TransactionMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface transactionMethodRepo extends JpaRepository<TransactionMethods, Long> {
}
