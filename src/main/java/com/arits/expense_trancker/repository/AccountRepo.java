package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Account;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account , Long> {

    Optional<Account> findByUser(User user);
}

