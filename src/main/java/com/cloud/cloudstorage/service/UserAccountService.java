package com.cloud.cloudstorage.service;

import com.cloud.cloudstorage.dto.UserAuthDto;
import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;

public interface UserAccountService {
    UserResponseDto registerNewUser(UserCreateDto userCreateDto, HttpServletRequest request);
    UserResponseDto authorizeUser(UserAuthDto userAuthDto, HttpServletRequest request);
}