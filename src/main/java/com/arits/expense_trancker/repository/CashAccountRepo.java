package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.CashAccount;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashAccountRepo extends JpaRepository<CashAccount, Long> {


    Optional<CashAccount> findByUser(User user);
}
