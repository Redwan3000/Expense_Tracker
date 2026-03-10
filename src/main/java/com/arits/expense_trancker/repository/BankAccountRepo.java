package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount,Long> {


}
