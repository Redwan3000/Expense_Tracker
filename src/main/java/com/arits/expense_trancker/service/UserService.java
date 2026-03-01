package com.arits.expense_trancker.service;

import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.Gender;
import com.arits.expense_trancker.entity.Permission;
import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.permissionRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {


    private final userRepo userRepo;
    private final roleRepo roleRepo;
    private final permissionRepo permissionRepo;
    private final genderRepo genderRepo;
    private final PasswordEncoder passwordEncoder;


    public UserDetailResponseDto getUserDetails(User currentUser) {
        log.info("Fetching current user info : {}", currentUser.getUsername());

        UserDetailResponseDto response = new UserDetailResponseDto();

        response.setUser_id(currentUser.getUserId());
        response.setUsername(currentUser.getUsername());
        response.setFirst_name(currentUser.getFirstName());
        response.setLast_name(currentUser.getLastName());
        response.setEmail(currentUser.getEmail());
        response.setPhone(currentUser.getPhone());
        response.setDob(currentUser.getDob());
        response.setGender(currentUser.getGender().getName());
        response.setRole(currentUser.getRole().getRoleName());

//        if (currentUser.getGender() != null) {
//            response.setGender(currentUser.getGender().getName());
//        }


//        if (currentUser.getRole() != null) {
//            response.setRole(currentUser.getRole().getRoleName());
//        }

        return response;
    }


    public ResponseEntity<?> defaultAdmin(UserRegisterRequestDto userRegisterRequestDto) {

        if (userRepo.findByUsername(userRegisterRequestDto.getUsername()).isPresent()) {
            System.out.println("ADMIN present please login");
        } else {

            Gender userGender = genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Invalid Gender"));
            Role userRole = roleRepo.findByRoleName("ADMIN").orElseThrow(() -> new RuntimeException("role not found"));
            Set<Permission> defaultPermission = userRole.getPermission();

            User newUser = userRepo.save(User.builder()
                    .firstName(userRegisterRequestDto.getFirst_name())
                    .lastName(userRegisterRequestDto.getLast_name())
                    .email(userRegisterRequestDto.getEmail())
                    .dob(userRegisterRequestDto.getDob())
                    .role(userRole)
                    .phone(userRegisterRequestDto.getPhone())
                    .gender(userGender)
                    .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                    .username(userRegisterRequestDto.getUsername())
                    .parent(null)
                    .permissions(new HashSet<>(defaultPermission))
                    .build());

            return ResponseEntity.ok(new UserRegisterResponseDto(newUser.getUserId(), newUser.getUsername()));
        }
    return ResponseEntity.ok().build();
    }







    public UserRegisterResponseDto createSubUser(UserRegisterRequestDto userRegisterRequestDto, Long userId) {

        if (userRepo.findByUsername(userRegisterRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        } else {


            Gender userGender = genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Invalid Gender"));
            Role userRole = roleRepo.findById(userRegisterRequestDto.getRole_id()).orElse(roleRepo.findByRoleName("SUBOWNER").orElseThrow());
            User currentUser = userRepo.findById(userId).orElseThrow();
            Set<Permission> defaultPermissions = userRole.getPermission();
            User newUser = userRepo.save(User.builder()
                    .firstName(userRegisterRequestDto.getFirst_name())
                    .lastName(userRegisterRequestDto.getLast_name())
                    .email(userRegisterRequestDto.getEmail())
                    .dob(userRegisterRequestDto.getDob())
                    .role(userRole)
                    .phone(userRegisterRequestDto.getPhone())
                    .gender(userGender)
                    .permissions(new HashSet<>(defaultPermissions))
                    .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                    .username(userRegisterRequestDto.getUsername())
                    .parent(currentUser)
                    .build());


            return new UserRegisterResponseDto(newUser.getUserId(), newUser.getUsername());
        }


    }


    public List<UserDetailResponseDto> getUserBySearch(String keyword) {


        List<User> users = userRepo.getUserBySearch(keyword);


        if (users.isEmpty()) {
            throw new RuntimeException("no user found with " + keyword);
        }


        return users.stream().map(this::getUserDetails).collect(Collectors.toList());


    }

    public List<SubuserListDto> getSubuserList(User currentUser) {

      List<User> subusers= userRepo.getUserByParentId(currentUser);
      return subusers.stream().map(n-> new SubuserListDto(n.getUsername(),n.getFirstName()+n.getLastName())).collect(Collectors.toList());

    }



        @Transactional
        public void softDeleteUser(Long userId) {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));


            List<User> subUsers = userRepo.getUserByParentId(user);


            for (User sub : subUsers) {
                userRepo.delete(sub);
            }


            userRepo.delete(user);
        }


    @Transactional
    public void softDeleteSubUser(User user,Long userId) {
        User currentuser = userRepo.findById(user.getUserId()).orElseThrow(()-> new RuntimeException("current user not found"));

        User subUser= userRepo.findById(userId).orElseThrow(()-> new RuntimeException("subuser not found"));

        if(subUser.getParent()==currentuser){

            userRepo.delete(subUser);

        }
        else {
            throw  new RuntimeException("subuser do not exist under your account");
        }
    }


    public List<alluserListDto> getAllUsers() {

        List<User>allUser= userRepo.findAll();

        List<alluserListDto> users =allUser.stream().map(u->new alluserListDto(u.getUserId(), u.getUsername())).collect(Collectors.toList());
        return users;
    }
    public List<alluserListDto> getAllDeletedUsers() {

        List<User> deletedUsers = userRepo.findAllDeletedUsers();


        return deletedUsers.stream()
                .map(user -> new alluserListDto(
                        user.getUserId(),
                        user.getUsername()
                ))
                .collect(Collectors.toList());
    }

    public UserRegisterRequestDto updateProfile(User user,UserRegisterRequestDto userRegisterRequestDto) {

        User currentuser = userRepo.findById(user.getUserId()).orElseThrow(()->new RuntimeException("user not found"));

        currentuser.setFirstName(userRegisterRequestDto.getFirst_name());
        currentuser.setLastName(userRegisterRequestDto.getLast_name());

        currentuser.setDob(userRegisterRequestDto.getDob());
        currentuser.setGender(genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(()->new RuntimeException("gender not found ")));
        currentuser.setPhone(userRegisterRequestDto.getPhone());

        currentuser.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));

        userRepo.save(currentuser);

        return UserRegisterRequestDto.builder()
                .first_name(currentuser.getFirstName())
                .last_name(currentuser.getLastName())
                .email(currentuser.getEmail())
                .phone(currentuser.getPhone())
                .dob(currentuser.getDob())
                .gender_id(currentuser.getGender().getGenderid())
                .username(currentuser.getUsername())
                .build();


    }
}








