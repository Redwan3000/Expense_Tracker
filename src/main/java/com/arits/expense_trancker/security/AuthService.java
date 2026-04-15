package com.arits.expense_trancker.security;


import com.arits.expense_trancker.dto.*;
import com.arits.expense_trancker.entity.*;

import com.arits.expense_trancker.repository.GenderRepo;
import com.arits.expense_trancker.repository.RoleRepo;
import com.arits.expense_trancker.repository.RolesDefaultPermissionsRepo;
import com.arits.expense_trancker.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final RolesDefaultPermissionsRepo rolesDefaultPermissionsRepo;


    public UserRegisterResponseDto register(User currentUser, UserRegisterRequestDto requestDto) {


        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        }


        Role role;
        Gender gender = genderRepo.findById(requestDto.getGenderId()).orElseThrow(() -> new RuntimeException("Gender does not exist"));

        if (currentUser == null) {
            role = roleRepo.findByName("OWNER").orElseThrow(() -> new RuntimeException("Role Not Found"));
        } else {
            String currentUserRole = currentUser.getRole().getName();
            if (currentUserRole.contains("ADMIN")) {
                if (requestDto.getRoleId() == null) {
                    throw new RuntimeException("admin must enter roleId");
                }
                role = roleRepo.findById(requestDto.getRoleId()).orElseThrow(() -> new RuntimeException("invalid roleId"));
                if (!role.getName().contains("OWNER") && !role.getName().contains("ADMIN")) {
                    throw new IllegalArgumentException("Admin cannot create tertiary and subowner without userId");
                }
            } else if (currentUserRole.contains("OWNER")) {
                if (requestDto.getRoleId() == null) {
                    role = roleRepo.findByName("SUBOWNER")
                            .orElseThrow(() -> new RuntimeException("Role SUBOWNER not found"));
                } else {
                    role = roleRepo.findById(requestDto.getRoleId())
                            .orElseThrow(() -> new RuntimeException("Role not found"));

                    if (!role.getName().equals("SUBOWNER") && !role.getName().equals("TERTIARY")) {
                        throw new RuntimeException("OWNERS can not create other OWNER or ADMIN accounts");
                    }
                }
            } else {
                throw new IllegalArgumentException("You Can Register");
            }

        }




    User newUser = userRepo.save(User.builder()
            .firstName(requestDto.getFirstName())
            .lastName(requestDto.getLastName())
            .email(requestDto.getEmail())
            .dob(requestDto.getDob())
            .role(role)
            .phone(requestDto.getPhone())
            .gender(gender)
            .parent((currentUser != null && currentUser.getRole().getName().equals("OWNER") ? currentUser : null))
            .password(passwordEncoder.encode(requestDto.getPassword()))
            .username(requestDto.getUsername())
            .build());

    List<RolesDefaultPermissions> defaultPermissions = rolesDefaultPermissionsRepo.findByRoleId(role.getId());


        List<UsersPermissions> userPerms = defaultPermissions.stream()
                .map(dp -> UsersPermissions.builder()
                        .user(newUser)
                        .permission(dp.getPermission())
                        .build())
                .toList();
        newUser.getUsersPermissions().addAll(userPerms);
        userRepo.save(newUser);

        return new UserRegisterResponseDto(newUser.getId(), newUser.getUsername());
    }


public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {

    Authentication authentication = authenticationManager
            .authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequestDto.getUsername(), userLoginRequestDto.getPassword())
            );

    User loginUser = (User) authentication.getPrincipal();

    String token = jwtUtils.createJwtToken(loginUser);

    return new UserLoginResponseDto(token, loginUser.getId());

}
}
