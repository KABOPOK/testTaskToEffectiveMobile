package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import generated.com.example.bankcards.api.AdminUserApi;
import generated.com.example.bankcards.api.model.UserAdminUpdateDto;
import generated.com.example.bankcards.api.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminUserController implements AdminUserApi {
    private final UserService userService;
    private final UserMapper userMapper;
    @Override
    public void blockUser(UUID id) {
        userService.blockUser(id);
    }

    @Override
    public void deleteUser(UUID id) {
        userService.deleteUser(id);
    }

    @Override
    public UserDto getUser(UUID id) {
        User user = userService.getUser(id);
        return userMapper.map(user);
    }

    @Override
    public void updateUser(UUID id, UserAdminUpdateDto userAdminUpdateDto) {
        User updatedUser = userMapper.mapFromUserAdminDto(userAdminUpdateDto);
        userService.updateUser(id, updatedUser);
    }
}
