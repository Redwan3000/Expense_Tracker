package com.arits.expense_trancker.controller;

import com.arits.expense_trancker.dto.SubuserListDto;
import com.arits.expense_trancker.dto.UserDetailResponseDto;
import com.arits.expense_trancker.dto.UserRegisterRequestDto;
import com.arits.expense_trancker.dto.UserRegisterResponseDto;
import com.arits.expense_trancker.entity.User;
import com.arits.expense_trancker.repository.genderRepo;
import com.arits.expense_trancker.repository.roleRepo;
import com.arits.expense_trancker.repository.userRepo;
import com.arits.expense_trancker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {


    private final UserService userService;
    private final userRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final genderRepo genderRepo;
    private final roleRepo roleRepo;


    @GetMapping("/user-info")
    @PreAuthorize("hasAuthority('user info')")

    public ResponseEntity<UserDetailResponseDto> getCurrentUserDetails(@AuthenticationPrincipal User currentUser) {

        log.info("current user info : {}", currentUser.getUsername());

        UserDetailResponseDto userDetails = userService.getUserDetails(currentUser);
        return ResponseEntity.ok(userDetails);
    }

    @GetMapping("/get-user-info/{keyword}")
    @PreAuthorize("hasAuthority('get any user')")
    public ResponseEntity<List<UserDetailResponseDto>> getUserInfo(@PathVariable("keyword") String keyword) {

        List<UserDetailResponseDto> userDetailResponseDtos = userService.getUserBySearch(keyword);
        return ResponseEntity.ok(userDetailResponseDtos);

    }


    @GetMapping("/subusers-list")
    @PreAuthorize("hasAuthority('subuser list')")
    public ResponseEntity<List<SubuserListDto>> getUserUserList(@AuthenticationPrincipal User currentUser){

        List<SubuserListDto> subusers= userService.getSubuserList(currentUser);
        return ResponseEntity.ok(subusers);

}

    @PostMapping("/create-subuser")
    @PreAuthorize("hasAuthority('create subuser')")
    public ResponseEntity<UserRegisterResponseDto> registerSubUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto, @AuthenticationPrincipal User user) {


        UserRegisterResponseDto userRegisterResponseDto = userService.createSubUser(userRegisterRequestDto, user.getUserId());
        return ResponseEntity.ok(userRegisterResponseDto);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('delete users') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User and associated sub-users have been soft-deleted successfully.");
    }

public ResponseEntity<?> getALlUser(){

        return ResponseEntity.ok(userService.getAllUsers());
}


}


