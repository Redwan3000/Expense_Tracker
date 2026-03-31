package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.MobileBanking;
import com.arits.expense_trancker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileBankingRepo extends JpaRepository<MobileBanking,Long> {


    boolean existsByProviderNameAndPhoneNumber(String providerName, String providerName1);

    MobileBanking findByUser(User user);

    Optional<MobileBanking> findByUserAndId(User user, Long mobileBankingId);

}
