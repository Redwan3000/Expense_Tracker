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


    public UserDetailResponseDto getUserDetail(User currentUser) {

        return UserDetailResponseDto.builder()
                .user_id(currentUser.getId())
                .username(currentUser.getUsername())
                .first_name(currentUser.getFirstName())
                .last_name(currentUser.getLastName())
                .email(currentUser.getEmail())
                .phone(currentUser.getPhone())
                .dob(currentUser.getDob())
                .gender(currentUser.getGender().getName())
                .role(currentUser.getRole().getName())
                .build();
    }
    public GetUserInfoDto getUserDetails(User currentUser) {

        GetUserInfoDto response= userRepo.getUserInfo(currentUser.getId());
        return response;
    }




  @Transactional
    public List<UserDetailResponseDto> getUserBySearch(String keyword) {


        List<User> users = userRepo.getUserBySearch(keyword);


        if (users.isEmpty()) {
            throw new RuntimeException("no user found with " + keyword);
        }


        return users.stream().map(this::getUserDetail).collect(Collectors.toList());


    }

    public List<SubuserListDto> getSubuserList(User currentUser) {

        List<SubuserListDto> subUsersList = userRepo.getSubusersListByParent(currentUser.getId());
        return subUsersList;

    }


    @Transactional
    public DeletedUserResponseDto softDeleteUser(Long userId) {

        User user= userRepo.findById(userId).orElseThrow(()->new RuntimeException("user not found"));

        DeletedUserResponseDto deletedUser= DeletedUserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();

        userRepo.softDeleteById(userId);
        userRepo.softDeleteSubUsers(userId);

return deletedUser;
    }


    @Transactional
    public DeletedUserResponseDto softDeleteSubUser(User user, Long userId) {
        User currentuser = userRepo.findById(user.getId()).orElseThrow(() -> new RuntimeException("current user not found"));

        User subUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("subuser not found"));

        DeletedUserResponseDto deletedUser = DeletedUserResponseDto.builder()
                .id(subUser.getId())
                .username(subUser.getUsername())
                .build();

        if (subUser.getParent() == currentuser) {

            userRepo.softDeleteSubUsersId(subUser.getId());

        } else {
            throw new RuntimeException("subuser do not exist under your account");
        }

        return deletedUser;
    }


    public List<AlluserListDto> getAllUsers() {

        List<User> allUser = userRepo.findAll();

        List<AlluserListDto> users = allUser.stream().map(u -> new AlluserListDto(u.getId(), u.getUsername())).collect(Collectors.toList());
        return users;
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








