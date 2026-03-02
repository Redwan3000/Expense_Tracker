package com.arits.expense_trancker.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseSchemaManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public  void softDeleteColumn (){
        jdbcTemplate.execute("alter table users_permissions add column if not exists is_deleted BOOLEAN default false");
    }

}
