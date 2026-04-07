package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Long> {



@Query(value = "select * from payment_method where name=:name",nativeQuery = true)
    Optional<PaymentMethod> findByMethodName(@Param("name") String name);

    @Query(value = "select * from payment_method where id=:tm_id",nativeQuery = true)
    Optional<PaymentMethod> findByMethodId(@Param("tm_id") long tm_id);

    Optional<PaymentMethod> findByNameIgnoreCase(String name);

}
