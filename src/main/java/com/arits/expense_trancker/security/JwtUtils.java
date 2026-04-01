package com.arits.expense_trancker.security;

import com.arits.expense_trancker.entity.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.expireationTime}")
    private Long expirationTime;

    public SecretKey key() {

        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String createJwtToken(User user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .signWith(key())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .compact();

    }


    public String getUsernameFromToken(String token) {

        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getJwtToken(HttpServletRequest request) {
        String beararToken = request.getHeader("Authorization");
        if (beararToken != null && beararToken.startsWith("Bearer ")) {
            return beararToken.substring(7);
        } else {
            return null;
        }

    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("invalid token {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("token expired {}", e.getMessage());
        }
        return false;
    }


}
