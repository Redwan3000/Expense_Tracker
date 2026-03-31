package com.arits.expense_trancker.service;

import com.arits.expense_trancker.entity.Gender;
import com.arits.expense_trancker.repository.GenderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenderService {

    public final GenderRepo genderRepo;


    public Gender genderSeeding( String name) {
        return genderRepo.findByName(name).orElseGet(() -> {
                    return genderRepo.save(Gender.builder()
                            .name(name)
                            .build());
                }
        );
    }

}
