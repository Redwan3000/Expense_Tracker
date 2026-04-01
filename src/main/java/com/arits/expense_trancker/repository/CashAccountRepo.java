package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.CashWallet;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CashAccountRepo extends JpaRepository<CashWallet, Long> {


    Optional<CashWallet> findByUser(User user);
}
