package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenderRepo extends JpaRepository<Gender,Long> {

    Optional<Gender> findByName(String name);

}
