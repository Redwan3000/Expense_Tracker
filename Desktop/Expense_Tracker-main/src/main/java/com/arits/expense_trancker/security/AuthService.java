package com.arits.expense_trancker.security;


import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserLoginResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.*;

import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final userRepo userRepo;
    private final genderRepo genderRepo;
    private final roleRepo roleRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;


    public UserRegisterResponseDto register(UserRegisterRequestDto userRegisterRequestDto) {

        if (userRepo.findByUsername(userRegisterRequestDto.getUsername()).isPresent()) {

            throw new IllegalArgumentException("user already exist ,,,,please login");
        } else {

            Gender userGender = genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Invalid Gender"));
            Role userRole = roleRepo.findByRoleName("OWNER").orElseThrow(() -> new RuntimeException("role not found"));


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
                    .usersPermissions(new HashSet<>())
                    .build());

            List<UsersPermissions> defailtPermission = userRole.getDefaultPermissions().stream()
                    .map(rolesPermission -> UsersPermissions.builder()
                            .user(newUser)
                            .permission(rolesPermission.getPermission()) // Extract the actual Permission
                            .isDeleted(false)
                            .build())
                    .toList();

            newUser.getUsersPermissions().addAll(defailtPermission);
            userRepo.save(newUser);
            return new UserRegisterResponseDto(newUser.getUserId(), newUser.getUsername());
        }

    }

    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword())
        );
        User loginUser = (User) authentication.getPrincipal();

        String token = jwtUtils.createJwtToken(loginUser);

        return new UserLoginResponseDto(token, loginUser.getUserId());

    }
}
