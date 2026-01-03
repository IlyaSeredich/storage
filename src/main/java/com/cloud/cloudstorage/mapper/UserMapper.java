package com.cloud.cloudstorage.mapper;

import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.UserResponseDto;
import com.cloud.cloudstorage.model.Role;
import com.cloud.cloudstorage.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", source = "password")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDto userCreateDto, String password, List<Role> roles);

    UserResponseDto toDto(String username);
}
