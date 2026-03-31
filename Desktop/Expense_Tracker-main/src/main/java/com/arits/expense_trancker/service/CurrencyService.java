package com.arits.expense_trancker.service;

import com.arits.expense_trancker.entity.Currency;
import com.arits.expense_trancker.repository.CurrencyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {


    private final CurrencyRepo currencyRepo;


    public Currency currencySeeding(String currencyName) {

        return currencyRepo.findByCurrencyName(currencyName).orElseGet(() -> {

            return currencyRepo.save(Currency.builder().currencyName(currencyName).build());

        });


    }


}
