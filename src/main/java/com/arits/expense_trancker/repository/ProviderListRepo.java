package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.ProviderList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProviderListRepo extends JpaRepository<ProviderList, Long> {


}
