package com.example.bankcards.mappers;

import com.example.bankcards.dto.AuthDataDto;
import com.example.bankcards.dto.UserAdminUpdateDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapperHelper.class)
public interface UserMapper {

    User map(UserDto userDto);
    @Mapping(target = "roles", source = "roles")
    UserDto map(User user);
    @Mapping(target = "roles", source = "roles")
    User mapFromUserAdminDto(UserAdminUpdateDto userAdminUpdateDto);
    User mapFromAuthData(AuthDataDto authDataDto);
    User map(AuthDataDto authDataDto);

}
