package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.BankAccount;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount,Long> {


    BankAccount findByUser(User user);

    Optional<BankAccount> findByUserAndId(User user, Long bankAccountId);

    @Modifying
    @Transactional
    @Query(value = "update bank_account set is_deleted = true, deleted_at = NOW() where id = :id", nativeQuery = true)
    void softDeleteBankAccount(@Param("id") Long id);


}