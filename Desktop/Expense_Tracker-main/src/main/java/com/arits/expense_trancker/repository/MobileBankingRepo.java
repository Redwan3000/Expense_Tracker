package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.MobileBanking;
import com.arits.expense_trancker.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileBankingRepo extends JpaRepository<MobileBanking,Long> {


    boolean existsByProviderNameAndPhoneNumber(String providerName, String providerName1);


    Optional<MobileBanking> findByUserAndId(User user, Long mobileBankingId);



    @Modifying
    @Transactional
    @Query(value = "update mobile_banking set is_deleted = true, deleted_at = NOW() where id = :id", nativeQuery = true)
    void softDeleteMobileBankingAccount(@Param("id") Long id);

}
