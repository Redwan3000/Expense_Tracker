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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
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

        response.setUser_id(currentUser.getUser_id());
        response.setUsername(currentUser.getUsername());
        response.setFirst_name(currentUser.getFirst_name());
        response.setLast_name(currentUser.getLast_name());
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
                    .first_name(userRegisterRequestDto.getFirst_name())
                    .last_name(userRegisterRequestDto.getLast_name())
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

            return ResponseEntity.ok(new UserRegisterResponseDto(newUser.getUser_id(), newUser.getUsername()));
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
                    .first_name(userRegisterRequestDto.getFirst_name())
                    .last_name(userRegisterRequestDto.getLast_name())
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


            return new UserRegisterResponseDto(newUser.getUser_id(), newUser.getUsername());
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
      return subusers.stream().map(n-> new SubuserListDto(n.getUsername(),n.getFirst_name()+n.getLast_name())).collect(Collectors.toList());

    }
}






