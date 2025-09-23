package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.UserService;
import com.example.bankcards.util.JwtTokenUtils;
import generated.com.example.bankcards.api.AuthApi;
import generated.com.example.bankcards.api.model.AuthDataDto;
import generated.com.example.bankcards.api.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.InstanceAlreadyExistsException;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserMapper userMapper;


    @Override
    public String loginUser(AuthDataDto authDataDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDataDto.getLogin(), authDataDto.getPassword()));
        UserDetails userDetails = userService.loadUserByUsername(authDataDto.getLogin());
        return jwtTokenUtils.generateToken(userDetails);
    }

    @Override
    public void registerUser(UserDto userDto)  {
        User user = userMapper.map(userDto);
        userService.createUser(user);
    }


    @GetMapping("/get")
    public String test() {
        return "Hello! Endpoint is public 🚀";
    }

}
