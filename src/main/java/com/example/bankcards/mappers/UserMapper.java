package com.example.bankcards.mappers;

import com.example.bankcards.entity.User;
import generated.com.example.bankcards.api.model.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User map(UserDto userDto);
}
