package com.arits.expense_trancker.security;


import com.arits.expense_trancker.dto.UserLoginRequestDto;
import com.arits.expense_trancker.dto.UserLoginResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.*;

import com.arits.expense_trancker.repository.AccountRepo;
import com.arits.expense_trancker.repository.GenderRepo;
import com.arits.expense_trancker.repository.RoleRepo;
import com.arits.expense_trancker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final GenderRepo genderRepo;
    private final RoleRepo roleRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepo accountRepo;


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

            if (accountRepo.findById(newUser.getUserId()).isPresent()) {


                throw new IllegalArgumentException("user's account already exist");

            } else {

                Account newAccount = Account.builder().user(userRepo.findByUsername(newUser.getUsername()).orElseThrow(() -> new RuntimeException("user not found")))
                        .currentBalance(BigDecimal.ZERO)
                        .totalIncome(BigDecimal.ZERO)
                        .totalExpense(BigDecimal.ZERO)
                        .updatedAt(LocalDateTime.now())
                        .build();
                accountRepo.save(newAccount);
            }
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
