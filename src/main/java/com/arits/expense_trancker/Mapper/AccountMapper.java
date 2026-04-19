package com.arits.expense_trancker.Mapper;


import com.arits.expense_trancker.dto.AccountResponseDto;
import com.arits.expense_trancker.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;



@Mapper(componentModel = "spring")
public interface AccountMapper {



    @Mapping(source = "id" ,target = "accountId")
    @Mapping(source = "currency.name", target= "currency")
    @Mapping(source = "accountType.name", target= "accountType")
    @Mapping(source = "paymentMethod.name", target= "paymentMethod")
    @Mapping(source = "accountDetails.accountNumber", target= "accountNumber")
    @Mapping(source = "accountDetails.nomineeName", target= "nomineeName")
    @Mapping(source = "accountDetails.provider.name", target= "providerName")
    @Mapping(source = "balance.amount", target= "currentBalance")
    @Mapping(source = "accountDetails.accountHolder", target= "accountHolder")
    AccountResponseDto toDto(Account account);


}
