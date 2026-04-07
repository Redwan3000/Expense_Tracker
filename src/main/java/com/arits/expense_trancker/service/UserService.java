package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {


    private final UserRepo userRepo;
    private final GenderRepo genderRepo;
    private final PasswordEncoder passwordEncoder;


    public List<UserDetailResponseDto> getUserDetails(User currentUser) {

        return userRepo.getUserByKeyword(currentUser.getUsername()).orElseThrow(() -> new RuntimeException("user not found"));

    }


    @Transactional
    public List<UserDetailResponseDto> getAnyUserBySearch(String keyword) {


        List<UserDetailResponseDto> users = userRepo.getUserByKeyword(keyword).orElseThrow(() -> new RuntimeException("user not found"));

        return users;


    }


    public List<SubuserListDto> getSubuserList(User currentUser) {

        return userRepo.getSubusersListByParent(currentUser.getId());

    }


    public softDeletedUserResponseDto softDeleteUser(Long userId) {

        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("user not found"));
        userRepo.delete(user);

        return softDeletedUserResponseDto.builder().id(user.getId()).build();
    }


    public softDeletedUserResponseDto softDeleteSubUser(User user, Long subUserId) {

        User subUser = userRepo.findByParentIdAndUserId(user.getId(), subUserId).orElseThrow(() -> new RuntimeException("SubUser Not Found"));
        userRepo.delete(subUser);

        return softDeletedUserResponseDto.builder().id(subUser.getId()).build();

    }


    public List<AllUserGroupWiseResponseDto> getAllUsers() {

        return userRepo.getAllOwner().stream()
                .map(owner -> AllUserGroupWiseResponseDto.builder()
                        .owner(owner)
                        .subowner(userRepo.getALlSubuserByOwnerId(owner.getUserId()))
                        .build()).collect(Collectors.toList());

    }


    public List<DeletedUsersListDto> getAllDeletedUsers() {

        return userRepo.findAllSoftDeletedUsers().stream().map(user -> DeletedUsersListDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .deletedAt(user.getDeletedAt())
                .build()).collect(Collectors.toList());
    }


    public UserRegisterRequestDto updateProfile(User currentuser, UserRegisterRequestDto dto) {

        currentuser.setFirstName(dto.getFirstName()==null?currentuser.getFirstName():dto.getFirstName());
        currentuser.setLastName(dto.getLastName()==null?currentuser.getLastName():dto.getLastName());
        currentuser.setDob(dto.getDob()==null?currentuser.getDob():dto.getDob());
        currentuser.setGender(dto.getGenderId()==null?currentuser.getGender():genderRepo.findById(dto.getGenderId()).orElseThrow(() -> new RuntimeException("gender not found ")));
        currentuser.setPhone(dto.getPhone()==null? currentuser.getEmail() : dto.getPhone());
        currentuser.setEmail(dto.getEmail()==null?currentuser.getEmail():dto.getEmail());
        currentuser.setPassword(passwordEncoder.encode(dto.getPassword()));


        userRepo.save(currentuser);

        return UserRegisterRequestDto.builder()
                .firstName(currentuser.getFirstName())
                .lastName(currentuser.getLastName())
                .email(currentuser.getEmail())
                .phone(currentuser.getPhone())
                .dob(currentuser.getDob())
                .genderId(currentuser.getGender().getId())
                .username(currentuser.getUsername())
                .build();

    }


    public RetriveUserResponseDto reviveUser(Long userId) {

        return userRepo.reviveUserById(userId).orElseThrow(()->new RuntimeException("user not found"));

    }


}








