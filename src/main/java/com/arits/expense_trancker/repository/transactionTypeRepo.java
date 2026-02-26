package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface transactionTypeRepo extends JpaRepository<TransactionType,Long> {


}
