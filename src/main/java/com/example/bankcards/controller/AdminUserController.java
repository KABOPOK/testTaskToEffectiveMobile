package com.example.bankcards.controller;

import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AdminUserService;
import generated.com.example.bankcards.api.AdminUserApi;
import generated.com.example.bankcards.api.model.UserAdminUpdateDto;
import generated.com.example.bankcards.api.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminUserController implements AdminUserApi {

    private final AdminUserService adminUserService;
    private final UserMapper userMapper;

    @Override
    public void blockUser(UUID id) {
        adminUserService.blockUser(id);
    }

    @Override
    public void deleteUser(UUID id) {
        adminUserService.deleteUser(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return adminUserService.getUsers().stream().map(userMapper::map).toList();
    }

    @Override
    public UserDto getUser(UUID id) {
        User user = adminUserService.getUser(id);
        return userMapper.map(user);
    }

    @Override
    public void updateUser(UUID id, UserAdminUpdateDto userAdminUpdateDto) {
        User updatedUser = userMapper.mapFromUserAdminDto(userAdminUpdateDto);
        adminUserService.updateUser(id, updatedUser);
    }

}
