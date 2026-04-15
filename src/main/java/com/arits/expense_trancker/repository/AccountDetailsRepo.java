package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.AccountDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailsRepo extends JpaRepository<AccountDetails, Long> {



}
