package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.AddBankAccountResponseDto;
import com.arits.expense_trancker.dto.AddMobileBankingRequestDto;
import com.arits.expense_trancker.dto.AddMobileBankingResponseDto;
import com.arits.expense_trancker.entity.MobileBanking;
import com.arits.expense_trancker.entity.MobileBankingAccountType;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.MobileBankingRepo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MobileBankingService {


    private final MobileBankingRepo mobileBankingRepo;


    public AddMobileBankingResponseDto addAccount(User user, AddMobileBankingRequestDto addMobileBankingRequestDto) {

        if(mobileBankingRepo.existsByProviderNameAndPhoneNumber (addMobileBankingRequestDto.getProviderName(),addMobileBankingRequestDto.getPhoneNumber()))
        {
            throw new RuntimeException("phone already exists");
        }
        MobileBanking newAccount= MobileBanking.builder()
                .providerName(addMobileBankingRequestDto.getProviderName())
                .accountType(MobileBankingAccountType.valueOf(addMobileBankingRequestDto.getAccountType()))
                .phoneNumber(addMobileBankingRequestDto.getPhoneNumber())
                .currentBalance(addMobileBankingRequestDto.getCurrentBalance())
                .user(user)
                .build();

        mobileBankingRepo.save(newAccount);

        return  AddMobileBankingResponseDto.builder()
                .id(newAccount.getId())
                .providerName(newAccount.getProviderName())
                .accountType(newAccount.getAccountType().toString())
                .phoneNumber(newAccount.getPhoneNumber())
                .currentBalance(newAccount.getCurrentBalance())
                .build();
    }
}
