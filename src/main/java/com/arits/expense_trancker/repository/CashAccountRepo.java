package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.CreateCashAccountRequestDto;
import com.arits.expense_trancker.dto.CreateCashAccountResponseDto;
import com.arits.expense_trancker.entity.CashAccount;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashAccountRepo extends JpaRepository<CashAccount,Long> {


}
