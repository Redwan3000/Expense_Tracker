package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {


    boolean existsByUserIdAndAccountNumber(Long id, String accountNumber);

    
    boolean existsByPaymentMethodAndId(Long paymentMethod, Long accountId);

    Optional<Account> findByUserIdAndId(Long id, Long accountId);
}
