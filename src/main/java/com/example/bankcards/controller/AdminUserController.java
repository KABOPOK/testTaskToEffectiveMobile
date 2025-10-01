package com.example.bankcards.controller;

import com.example.bankcards.dto.UserAdminUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.mappers.UserMapper;
import com.example.bankcards.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/user")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserMapper userMapper;

    @PutMapping("/update/{id}")
    @Operation(summary = "Обновить данные пользователя")
    public void updateUser(@PathVariable UUID id,
                           @Valid @RequestBody UserAdminUpdateDto userAdminUpdateDto) {
        User updatedUser = userMapper.mapFromUserAdminDto(userAdminUpdateDto);
        adminUserService.updateUser(id, updatedUser);
    }

    @PutMapping("/block/{id}")
    @Operation(summary = "Заблокировать пользователя")
    public void blockUser(@PathVariable UUID id) {
        adminUserService.blockUser(id);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "Получить пользователя по ID")
    public UserDto getUser(@PathVariable UUID id) {
        User user = adminUserService.getUser(id);
        return userMapper.map(user);
    }

    @GetMapping("/get_all_users")
    @Operation(summary = "Получить всех пользователей")
    public List<UserDto> getAllUsers() {
        return adminUserService.getUsers()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить пользователя")
    public void deleteUser(@PathVariable UUID id) {
        adminUserService.deleteUser(id);
    }


}
