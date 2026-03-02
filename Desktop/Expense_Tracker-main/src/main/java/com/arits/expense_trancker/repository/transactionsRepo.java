package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface transactionsRepo extends JpaRepository<Transactions,Long> {
}
