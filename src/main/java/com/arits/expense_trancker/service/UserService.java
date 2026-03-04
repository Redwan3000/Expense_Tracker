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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;
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
    private final transactionTypeRepo transactionTypeRepo;
    private final transactionMethodRepo transactionMethodRepo;
    private final transactionsRepo transactionsRepo;


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


        return response;
    }


    public ResponseEntity<?> defaultAdmin(UserRegisterRequestDto userRegisterRequestDto) {

        if (userRepo.findByUsername(userRegisterRequestDto.getUsername()).isPresent()) {
            System.out.println("ADMIN present please login");
        } else {

            Gender userGender = genderRepo.findById(userRegisterRequestDto.getGender_id()).orElseThrow(() -> new RuntimeException("Invalid Gender"));
            Role userRole = roleRepo.findByRoleName("ADMIN").orElseThrow(() -> new RuntimeException("role not found"));

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

            Set<UsersPermissions> defaultPermission = userRole.getDefaultPermissions().stream()
                    .map(rolesPermission -> UsersPermissions.builder()
                            .user(newUser)
                            .permission(rolesPermission.getPermission())
                            .isDeleted(false).build()).collect(Collectors.toSet());

            newUser.setUsersPermissions(defaultPermission);
            userRepo.save(newUser);
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


            Set<UsersPermissions> defaultPermission = userRole.getDefaultPermissions().stream()
                    .map(rolesPermission -> UsersPermissions.builder()
                            .user(newUser)
                            .permission(rolesPermission.getPermission())
                            .isDeleted(false).build()).collect(Collectors.toSet());

            newUser.setUsersPermissions(defaultPermission);
            userRepo.save(newUser);
            return new UserRegisterResponseDto(newUser.getUserId(), newUser.getUsername());
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

        List<User> subusers = userRepo.getUserByParentId(currentUser);
        return subusers.stream().map(n -> new SubuserListDto(n.getUsername(), n.getFirstName() + n.getLastName())).collect(Collectors.toList());

    }


    @Transactional
    public void softDeleteUser(Long userId) {

        if (!userRepo.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        userRepo.softDeleteById(userId);
        userRepo.softDeleteSubUsers(userId);


    }


    @Transactional
    public void softDeleteSubUser(User user, Long userId) {
        User currentuser = userRepo.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("current user not found"));

        User subUser = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("subuser not found"));

        if (subUser.getParent() == currentuser) {

            userRepo.softDeleteSubUsersId(subUser.getUserId());

        } else {
            throw new RuntimeException("subuser do not exist under your account");
        }
    }


    public List<alluserListDto> getAllUsers() {

        List<User> allUser = userRepo.findAll();

        List<alluserListDto> users = allUser.stream().map(u -> new alluserListDto(u.getUserId(), u.getUsername())).collect(Collectors.toList());
        return users;
    }


    public List<DeletedUsersListDto> getAllDeletedUsers() {

        List<User> deletedUsers = userRepo.findAllDeletedUsers();

        if (deletedUsers.isEmpty()) {
            return Collections.emptyList();
        }

        return deletedUsers.stream().filter(Objects::nonNull).map(user -> {
            DeletedUsersListDto dto = new DeletedUsersListDto();
            dto.setId(user.getUserId());
            dto.setUsername(user.getUsername());
            dto.setDeletedAt(user.getDeletedAt());
            return dto;
        }).collect(Collectors.toList());
    }


    public UserRegisterRequestDto updateProfile(User user, UserRegisterRequestDto userRegisterRequestDto) {

        User currentuser = userRepo.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("user not found"));

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
                .gender_id(currentuser.getGender().getGenderid())
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
        return new RetriveUserResponseDto(user.getUserId(), user.getUsername());
    }

    @Transactional
    public AddTransactionRequestDTO addTransaction(User user, AddTransactionRequestDTO addTransactionRequestDTO, MultipartFile multipartFile) {

        String savedFilePath = null;
        if (multipartFile != null && !multipartFile.isEmpty()) {

            try {

                String uploadDir = "uploads/invoices/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

                Path filePath = Paths.get(uploadDir + fileName);

                Files.write(filePath, multipartFile.getBytes());

                savedFilePath = filePath.toString();

            } catch (IOException e) {
                throw new RuntimeException("failed to store the invoices" + e.getMessage());
            }
        }
        Transactions transactions = Transactions.builder()
                .amount(addTransactionRequestDTO.getAmount())
                .itemName(addTransactionRequestDTO.getItemName())
                .description(addTransactionRequestDTO.getDescription())
                .date(LocalDate.now())
                .user(user)
                .transactionType(transactionTypeRepo.findById(addTransactionRequestDTO.getTType()).orElseThrow(() -> new RuntimeException("transaction Type not found")))
                .transactionMethods(transactionMethodRepo.findByMethodId(addTransactionRequestDTO.getTMethod()).orElseThrow(() -> new RuntimeException("method not found")))
                .invoicePath(savedFilePath)
                .build();

        transactionsRepo.save(transactions);

        return new AddTransactionResponseDTO(transactions.getAmount(),transactions.getTransactionMethods().getMethodName(),transactions.getTransactionType().getTypeName(),transactions.getDescription(),transactions.getItemName(),transactions.getInvoicePath());

    }
}








