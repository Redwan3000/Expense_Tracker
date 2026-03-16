package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.BankAccount;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount,Long> {


    BankAccount findByUser(User user);

   Optional<BankAccount> findByUserAndId(User user, Long bankAccountId);

}
