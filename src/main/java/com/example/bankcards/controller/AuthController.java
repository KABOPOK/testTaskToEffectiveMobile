package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthDataDto;
import com.example.bankcards.dto.TokenDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/user")
public class AuthController {

    private final AdminUserService adminUserService;
    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/auth")
    @Operation(summary = "Аутентификация и получение токена")
    public TokenDto loginUser(@Valid @RequestBody AuthDataDto authDataDto) {
        User user = userMapper.mapFromAuthData(authDataDto);
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(authService.getToken(user));
        return tokenDto;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя")
    public void registerUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.map(userDto);
        adminUserService.createUser(user);
    }


}
