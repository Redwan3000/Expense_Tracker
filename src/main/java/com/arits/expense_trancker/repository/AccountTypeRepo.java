package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.AccountType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTypeRepo extends JpaRepository<AccountType,Long> {


    Optional<AccountType> findByNameIgnoreCase(String accountType);
}
