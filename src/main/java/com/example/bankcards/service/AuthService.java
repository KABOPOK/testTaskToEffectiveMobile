package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.EntityBlockedException;
import com.example.bankcards.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    public String getToken(User user) {
        User savedUser = userService.getUser(user.getLogin());
        if(savedUser.getStatus().equals("BLOCKED")){
            throw new EntityBlockedException(format("entity with id %s is BLOCKED", savedUser.getId()));
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword())
        );
        UserDetails userDetails = userService.loadUserByUsername(user.getLogin());
        return jwtTokenUtils.generateToken(userDetails);
    }
}
