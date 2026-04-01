package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Bank;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepo extends JpaRepository<Bank,Long> {


    Bank findByUser(User user);


    Optional<Bank> findByUserAndId(User user, long id);

    @Modifying
    @Transactional
    @Query(value = "update bank set is_deleted = true, deleted_at = NOW() where id = :id", nativeQuery = true)
    void softDeleteBankAccount(@Param("id") Long id);

}