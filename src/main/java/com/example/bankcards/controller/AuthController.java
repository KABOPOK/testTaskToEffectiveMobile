package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.AdminUserService;
import generated.com.example.bankcards.api.AuthApi;
import generated.com.example.bankcards.api.model.AuthDataDto;
import generated.com.example.bankcards.api.model.TokenDto;
import generated.com.example.bankcards.api.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {
    private final AdminUserService adminUserService;
    private final AuthService authService;
    private final UserMapper userMapper;


    @Override
    public TokenDto loginUser(AuthDataDto authDataDto) {
        User user = userMapper.mapFromAuthData(authDataDto);
        TokenDto tokenDto = new TokenDto();
        tokenDto.setToken(authService.getToken(user));
        return tokenDto;
    }

    @Override
    public void registerUser(UserDto userDto)  {
        User user = userMapper.map(userDto);
        adminUserService.createUser(user);
    }

}
