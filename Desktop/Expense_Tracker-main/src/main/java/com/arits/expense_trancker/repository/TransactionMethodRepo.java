package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.TransactionMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionMethodRepo extends JpaRepository<TransactionMethods, Long> {



@Query(value = "select * from transaction_methods where method_name=:name",nativeQuery = true)
    Optional<TransactionMethods> findByMethodName(@Param("name") String name);

    @Query(value = "select * from transaction_methods where tm_id=:tm_id",nativeQuery = true)
    Optional<TransactionMethods> findByMethodId(@Param("tm_id") long tm_id);

}
