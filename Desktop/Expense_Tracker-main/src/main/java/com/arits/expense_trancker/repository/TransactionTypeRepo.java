package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionTypeRepo extends JpaRepository<TransactionType,Long> {


    Optional<TransactionType> findByTypeName(String name);
}
