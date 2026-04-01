package com.arits.expense_trancker.repository;


import com.arits.expense_trancker.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CurrencyRepo extends JpaRepository<Currency , Long> {


    Optional<Currency> findByName(String currency);

}
