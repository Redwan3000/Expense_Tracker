package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;
import com.arits.expense_trancker.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final RoleRepo roleRepo;
    private final PermissionRepo permissionRepo;
    private final GenderRepo genderRepo;
    private final PasswordEncoder passwordEncoder;


    public UserDetailResponseDto getUserDetails(User currentUser) {

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


    public UserRegisterResponseDto defaultAdmin(UserRegisterRequestDto requestDto) {


        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        }


        Gender gender = genderRepo.findById(requestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Gender does not exist"));
        Role role = roleRepo.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role does not exist"));


        User newUser = userRepo.save(User.builder()
                .firstName(requestDto.getFirst_name())
                .lastName(requestDto.getLast_name())
                .email(requestDto.getEmail())
                .dob(requestDto.getDob())
                .role(role)
                .phone(requestDto.getPhone())
                .gender(gender)
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .username(requestDto.getUsername())
                .build());

        List<UsersPermissions> defaultPermissions = role.getRolesDefaultPermissions().stream()
                .map(r -> UsersPermissions.builder()
                        .user(newUser)
                        .permission(r.getPermission())
                        .build())
                .toList();

        newUser.getUsersPermissions().addAll(defaultPermissions);
        userRepo.save(newUser);

        return new UserRegisterResponseDto(newUser.getId(), newUser.getUsername());
    }






    public UserRegisterResponseDto createSubUser(UserRegisterRequestDto userRegisterRequestDto, Long userId) {

        if (userRepo.findByUsername(userRegisterRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        } else {


            Gender userGender = genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Invalid Gender"));
            Role userRole = roleRepo.findById(userRegisterRequestDto.getRole_id()).orElse(roleRepo.findByName("SUBOWNER").orElseThrow());
            User currentUser = userRepo.findById(userId).orElseThrow();

            User newUser = userRepo.save(User.builder()
                    .firstName(userRegisterRequestDto.getFirst_name())
                    .lastName(userRegisterRequestDto.getLast_name())
                    .email(userRegisterRequestDto.getEmail())
                    .dob(userRegisterRequestDto.getDob())
                    .role(userRole)
                    .phone(userRegisterRequestDto.getPhone())
                    .gender(userGender)
                    .usersPermissions(new HashSet<>())
                    .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                    .username(userRegisterRequestDto.getUsername())
                    .parent(currentUser)
                    .build());


            Set<UsersPermissions> defaultPermission = userRole.getRolesDefaultPermissions().stream()
                    .map(rolesPermission -> UsersPermissions.builder()
                            .user(newUser)
                            .permission(rolesPermission.getPermission())
                            .isDeleted(false).build()).collect(Collectors.toSet());

            newUser.setUsersPermissions(defaultPermission);
            userRepo.save(newUser);
            return new UserRegisterResponseDto(newUser.getId(), newUser.getUsername());
        }


    }


    @Transactional
    public List<UserDetailResponseDto> getUserBySearch(String keyword) {


        List<User> users = userRepo.getUserBySearch(keyword);


        if (users.isEmpty()) {
            throw new RuntimeException("no user found with " + keyword);
        }


        return users.stream().map(this::getUserDetails).collect(Collectors.toList());


    }

    public List<SubuserListDto> getSubuserList(User currentUser) {

        List<User> subusers = userRepo.getUserByParentId(currentUser.getId());
        return subusers.stream().map(n -> new SubuserListDto(n.getUsername(), n.getFirstName() + n.getLastName())).collect(Collectors.toList());

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

        currentuser.setFirstName(userRegisterRequestDto.getFirst_name());
        currentuser.setLastName(userRegisterRequestDto.getLast_name());

        currentuser.setDob(userRegisterRequestDto.getDob());
        currentuser.setGender(genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("gender not found ")));
        currentuser.setPhone(userRegisterRequestDto.getPhone());

        currentuser.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));

        userRepo.save(currentuser);

        return UserRegisterRequestDto.builder()
                .first_name(currentuser.getFirstName())
                .last_name(currentuser.getLastName())
                .email(currentuser.getEmail())
                .phone(currentuser.getPhone())
                .dob(currentuser.getDob())
                .gender_id(currentuser.getGender().getId())
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








