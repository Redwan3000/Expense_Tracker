package com.arits.expense_trancker.service;

import com.arits.expense_trancker.entity.Gender;
import com.arits.expense_trancker.entity.TransactionMethods;
import com.arits.expense_trancker.entity.TransactionType;
import com.arits.expense_trancker.repository.transactionMethodRepo;
import com.arits.expense_trancker.repository.transactionTypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {


    private final transactionTypeRepo transactionTypeRepo;
    private final transactionMethodRepo transactionMethodRepo;

    public TransactionMethods tmSeedding(String name){

            return transactionMethodRepo.findByMethodName(name).orElseGet(() -> {
                        return transactionMethodRepo.save(TransactionMethods.builder()
                                        .methodName(name)
                                .build());
                    }
            );


}

public TransactionType ttSeeding(String name){


    return transactionTypeRepo.findByTypeName(name).orElseGet(() -> {
                return transactionTypeRepo.save(TransactionType.builder()
                        .typeName(name)
                        .build());
            }
    );

}

}
