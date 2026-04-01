package com.arits.expense_trancker.security;


import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;

import com.arits.expense_trancker.repository.GenderRepo;
import com.arits.expense_trancker.repository.RoleRepo;
import com.arits.expense_trancker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final JwtUtils jwtUtils;
    private final GenderRepo genderRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public UserRegisterResponseDto register(UserRegisterRequestDto requestDto) {


        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        }


        Gender gender = genderRepo.findById(requestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Gender does not exist"));
        Role role = roleRepo.findByName("OWNER").orElseThrow(() -> new RuntimeException("Role does not exist"));


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




    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {

        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword())
                );

        User loginUser = (User)authentication.getPrincipal();

        String token = jwtUtils.createJwtToken(loginUser);

        return new UserLoginResponseDto(token, loginUser.getId());

    }
}
