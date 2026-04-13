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



    public UserRegisterResponseDto register(User currentUser ,UserRegisterRequestDto requestDto) {


        if (userRepo.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("user already exist ,,,,please login");
        }


        Role role;
        Gender gender = genderRepo.findById(requestDto.getGenderId()).orElseThrow(() -> new RuntimeException("Gender does not exist"));


        if(currentUser== null && requestDto.getRoleId()==null){

            role=roleRepo.findByName("OWNER").orElseThrow(()->
                    new RuntimeException("ERROR Getting Owner Role"));

        }
        else if (requestDto.getRoleId()!=null && (currentUser==null ||currentUser.getRole().getName().contains("ADMIN")) ) {
            role= roleRepo.findById(requestDto.getRoleId()).orElseThrow(()->
                    new RuntimeException("Role Does Not Exist"));

            if(role.getName().contains("SUBOWNER")|| role.getName().contains("TERTIARY")){
                throw new IllegalArgumentException("ADMINS Cannot Create SUBOWNER Or TERTIARY Accounts");
            }
        }

        else if (currentUser.getRole().getName().contains("OWNER")) {

            if(requestDto.getRoleId()==null){
                role = roleRepo.findById(2L).orElseThrow(()->new RuntimeException("Role Does Not Exist"));

            }else if(requestDto.getRoleId()==2L || requestDto.getRoleId()==3L){
                role= roleRepo.findById(requestDto.getRoleId()).orElseThrow(()->new RuntimeException("Role Does Not Exist"));
            }else {
                throw new RuntimeException("OWNERS Can Not Create OWNERS Or ADMINS Account");
            }

        }
        else {
            throw new IllegalArgumentException("You Cannot Register");
        }


        User newUser = userRepo.save(User.builder()
                .firstName(requestDto.getFirstName())
                .lastName(requestDto.getLastName())
                .email(requestDto.getEmail())
                .dob(requestDto.getDob())
                .role(role)
                .phone(requestDto.getPhone())
                .gender(gender)
                .parent((currentUser != null && currentUser.getRole().getName().contains("OWNER") ? currentUser : null))
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .username(requestDto.getUsername())
                .build());

List<RolesDefaultPermissions> defaultPermissions= rolesDefaultPermissionsRepo.findByRoleId(role.getId());


        newUser.getUsersPermissions().addAll(defaultPermissions.stream()
                .map(r -> UsersPermissions.builder()
                        .user(newUser)
                        .permission(r.getPermission())
                        .build())
                .toList());

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
