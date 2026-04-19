package com.arits.expense_trancker.security;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class UrlSecurityProperties {

    @Getter
    @Setter
    private List<UrlRule> urlRules= new ArrayList<>();


    @Getter
    @Setter
    public static class UrlRule{

        private String pattern;
        private String method;
        private List<String> anyOfPermissions  =new ArrayList<>();

    }
}
