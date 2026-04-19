package com.arits.expense_trancker;

import com.arits.expense_trancker.security.UrlSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(UrlSecurityProperties.class)
public class ExpenseTranckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseTranckerApplication.class, args);
    }

}
