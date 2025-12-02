package com.edvalld.projektarbete.security.jwt;


import com.edvalld.projektarbete.user.CustomUser;
import com.edvalld.projektarbete.user.authority.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger (JwtUtils .class);

    //@Value("${BASE64_SECRET_KEY}")
    private final String base64EncodedSecretKey = "nQSOuRhdgmQlKuioG5uJkzAegqGHqMGAoDtYtZS3u3ApHXfvrdvPF0Mc0IOnu8yL3W77Rxctu4zF" ;
    private final byte[] keyBytes =
            Base64.getDecoder ().decode(base64EncodedSecretKey );
    private final SecretKey key = Keys.hmacShaKeyFor(keyBytes );
    // JWT expiration time (1 hour)
    private final int jwtExpirationMs = (int) TimeUnit.HOURS.toMillis (1);

    public String generateJwtToken (CustomUser customUser ) {
        logger.debug("Generating JWT for user: {} with roles: {}" ,
                customUser.getUsername (), customUser.getRoles());
        List<String> roles = customUser.getRoles().stream().map(
                userRole -> userRole.getRoleName()
        ).toList();
        String token = Jwts.builder()
                .subject(customUser.getUsername())
                .claim("authorities", roles)
                .issuedAt (new Date())
                .expiration (new Date(System.currentTimeMillis () + jwtExpirationMs ))
                .signWith(key)
                .compact();
        logger.info("JWT generated successfully for user: {}" , customUser.getUsername ());
        return token;
    }

    public String getUsernameFromJwtToken (String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith (key)
                    .build()
                    .parseSignedClaims (token)
                    .getPayload ();
            String username = claims.getSubject ();
            logger.debug("Extracted username '{}' from JWT token" , username );
            return username;
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}" , e.getMessage ());
            return null;
        }
    }

    public boolean validateJwtToken (String authToken ) {
        try {
            Jwts.parser()
                    .verifyWith (key)
                    .build()
                    .parseSignedClaims (authToken );
            logger.debug("JWT validation succeeded" );
            return true;
        } catch (Exception e) {
            logger.error("JWT validation failed: {}" , e.getMessage ());
        }
        return false;
    }
    public Set<UserRole > getRolesFromJwtToken (String token) {
        Claims claims = Jwts.parser ()
                .verifyWith (key)
                .build()
                .parseSignedClaims (token)
                .getPayload ();
        List<?> authoritiesClaim = claims. get("authorities" , List.class);
        if (authoritiesClaim == null || authoritiesClaim. isEmpty ()) {
            logger .warn("No authorities found in JWT token" );
            return Set.of();
        }
        // Convert each string like "ROLE_USER" -> UserRole.USER
        Set<UserRole> roles = authoritiesClaim. stream ()
                .filter (String .class::isInstance ) // keep only strings
                .map(String .class::cast)
                .map(role -> role.replace ("ROLE_" , "")) // remove prefix if necessary
                .map(String ::toUpperCase )
                .map(UserRole ::valueOf ) // map to my enum
                .collect (Collectors.toSet());
        logger .debug("Extracted roles from JWT token: {}" , roles );
        return roles;
    }
}

