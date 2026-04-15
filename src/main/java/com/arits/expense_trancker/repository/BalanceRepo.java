package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepo extends JpaRepository<Balance, Long> {
}
