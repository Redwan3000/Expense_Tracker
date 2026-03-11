package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.BankAccount;
import com.arits.expense_trancker.entity.MobileBanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileBankingRepo extends JpaRepository<MobileBanking,Long> {


    boolean existsByProviderNameAndPhoneNumber(String providerName, String providerName1);
}
