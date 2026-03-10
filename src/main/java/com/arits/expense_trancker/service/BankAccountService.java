package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.AddBankAccountRequestDTO;
import com.arits.expense_trancker.entity.BankAccount;
import com.arits.expense_trancker.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BankAccountService  {


    public String addAccount(User user, AddBankAccountRequestDTO addBankAccountRequestDTO) {


        BankAccount.builder()

                .bankName(addBankAccountRequestDTO.getBankName())
                .bankBranch(addBankAccountRequestDTO.getBankBranch())
                .accountNumber(addBankAccountRequestDTO.getAccountNumber())
                .accountType()
                .currency()
                .currentBalance()
                .user()
                .build()


    }
}
