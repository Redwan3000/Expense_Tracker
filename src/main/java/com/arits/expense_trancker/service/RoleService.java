package com.arits.expense_trancker.service;

import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.repository.roleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

public final roleRepo roleRepo;

    public Role roleSeeding(String name) {
        return roleRepo.findByRoleName(name).orElseGet(() -> {
                    return roleRepo.save(Role.builder().roleName(name).build());
            }

        );
    }


}
