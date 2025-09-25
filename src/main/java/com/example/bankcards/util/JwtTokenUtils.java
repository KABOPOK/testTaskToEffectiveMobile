package com.example.bankcards.util;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityBlockedException;
import com.example.bankcards.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    private final UserRepository userRepository;

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        Set<String> rolesSet = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        claims.put("roles", rolesSet);

        Date issued = new Date();
        Date expiration = new Date(issued.getTime() + lifetime.toMillis());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issued)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private Claims getClaims(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody();
    }

    private String getUserLogin(String jwt) {
        return  getClaims(jwt).getSubject();
    }

    private List<String> getRoles(String token) {
        List<?> rawRoles = getClaims(token).get("roles", List.class);
        if (rawRoles == null) return List.of();
        return rawRoles.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toList());
    }

    public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String jwt) {
        String login = getUserLogin(jwt);
        User savedUser = userRepository.findByLogin(login).orElseThrow(
                () -> new EntityNotFoundException(format("Entity with id %s not found", login))
        );
        if(savedUser.getStatus().equals("BLOCKED")){
            throw new EntityBlockedException(format("entity with id %s is BLOCKED", savedUser.getId()));
        }
        return new UsernamePasswordAuthenticationToken(login, null,
                getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
