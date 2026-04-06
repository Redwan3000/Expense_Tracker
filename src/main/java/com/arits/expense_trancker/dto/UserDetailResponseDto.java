package com.arits.expense_trancker.dto;
import java.time.LocalDate;



public interface UserDetailResponseDto {

     Long getUserId();
     String getUsername();
     String getFirstName();
     String getLastName();
     String getEmail();
     String getPhone();
     LocalDate getDob();
     String getGender();
     String getRole();
     Long getParentId();

}
