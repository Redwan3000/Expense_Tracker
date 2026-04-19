package com.arits.expense_trancker.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class UrlPermissionChecker {

    private final UrlSecurityProperties urlSecurityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public boolean isAllowed(String requestUrl, String requestMethod, Collection<? extends GrantedAuthority> authorities) {

        for (UrlSecurityProperties.UrlRule rules : urlSecurityProperties.getUrlRules()) {
            boolean urlMatch = pathMatcher.match(rules.getPattern(), requestUrl);
            boolean methodMatch = rules.getMethod().equalsIgnoreCase(requestMethod);
            boolean hasPermission = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> rules.getAnyOfPermissions()
                            .contains(a));

            if (urlMatch && methodMatch && !hasPermission) {
                return false;
            }
        }


        return true;
    }

}
