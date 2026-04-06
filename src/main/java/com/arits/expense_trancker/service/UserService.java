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

        List<UserDetailResponseDto> users = userRepo.getUserByKeyword(currentUser.getUsername()).orElseThrow(()->new RuntimeException("user not found"));

        return users;
    }


    @Transactional
    public List<UserDetailResponseDto> getAnyUserBySearch(String keyword) {


        List<UserDetailResponseDto> users = userRepo.getUserByKeyword(keyword).orElseThrow(()->new RuntimeException("user not found"));

        return users;


    }


    public List<SubuserListDto> getSubuserList(User currentUser) {
        List<SubuserListDto> subUsersList = userRepo.getSubusersListByParent(currentUser.getId());

        return subUsersList;

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

        List<UserDetailResponseDto>allUsers= userRepo.getAllUsersDetail();


        List<UserDetailResponseDto>owner= userRepo.getUserByKeyword("owner").orElseThrow(()->new RuntimeException("Owner Not Found"));


        List<UserDetailResponseDto>subowner= userRepo.getUserByKeyword("subowner").orElseThrow(()->new RuntimeException("Owner Not Found"));

        List<AllUserGroupWiseResponseDto> temp =   AllUserGroupWiseResponseDto.builder()
                .owner(owner)
                .subowner(subowner)
                .build();



    }


    public List<DeletedUsersListDto> getAllDeletedUsers() {

        List<User> deletedUsers = userRepo.findAllDeletedUsers();

        if (deletedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        return deletedUsers.stream().filter(Objects::nonNull).map(user -> {
            DeletedUsersListDto dto = new DeletedUsersListDto();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setDeletedAt(user.getDeletedAt());
            return dto;
        }).collect(Collectors.toList());
    }


    public UserRegisterRequestDto updateProfile(User user, UserRegisterRequestDto userRegisterRequestDto) {

        User currentuser = userRepo.findById(user.getId()).orElseThrow(() -> new RuntimeException("user not found"));

        currentuser.setFirstName(userRegisterRequestDto.getFirstName());
        currentuser.setLastName(userRegisterRequestDto.getLastName());

        currentuser.setDob(userRegisterRequestDto.getDob());
        currentuser.setGender(genderRepo.findById(userRegisterRequestDto.getGenderId()).orElseThrow(() -> new RuntimeException("gender not found ")));
        currentuser.setPhone(userRegisterRequestDto.getPhone());

        currentuser.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));

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

    public RetriveUserResponseDto retriveUser(Long userId) {

        User user = userRepo.findUserFromSoftDelete(userId).orElseThrow(() -> new RuntimeException("USER NOT FOUND IN SOFT DELETE"));
        List<User> subusers = userRepo.findSubUserByParentId(userId);

        user.setDeleted(false);
        user.setDeletedAt(null);
        subusers.forEach(p -> {
            p.setDeleted(false);
            p.setDeletedAt(null);
        });
        log.info("user and his subusers retrieved");
        return new RetriveUserResponseDto(user.getId(), user.getUsername());
    }


}








