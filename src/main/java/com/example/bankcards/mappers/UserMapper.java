package com.example.bankcards.mappers;

import com.example.bankcards.entity.User;
import generated.com.example.bankcards.api.model.AuthDataDto;
import generated.com.example.bankcards.api.model.UserAdminUpdateDto;
import generated.com.example.bankcards.api.model.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = UserMapperHelper.class)
public interface UserMapper {

    User map(UserDto userDto);
    @Mapping(target = "roles", source = "roles")
    UserDto map(User user);
    @Mapping(target = "roles", source = "roles")
    User mapFromUserAdminDto(UserAdminUpdateDto userAdminUpdateDto);
    User mapFromAuthData(AuthDataDto authDataDto);
}
