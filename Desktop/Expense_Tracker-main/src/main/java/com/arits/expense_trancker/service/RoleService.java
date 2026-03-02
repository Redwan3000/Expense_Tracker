package com.arits.expense_trancker.service;

import com.arits.expense_trancker.entity.Role;
import com.arits.expense_trancker.repository.roleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

public final roleRepo roleRepo;

    public void roleSeeding(String name) {

        Optional<Role>existingRole= roleRepo.findByNameIncludingDeleted(name);

        if(existingRole.isPresent()){
            Role role= existingRole.get();
            if(role.isDeleted()){
                role.setDeleted(false);
                roleRepo.save(role);
            }
        }else {
            roleRepo.save(Role.builder()
                    .roleName(name)
                    .isDeleted(false)
                    .build());
        }

    }


}
