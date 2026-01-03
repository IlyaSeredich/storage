package com.cloud.cloudstorage.service;

import com.cloud.cloudstorage.dto.UserResponseDto;
import org.springframework.security.core.userdetails.User;

public interface CurrentUserService {
    UserResponseDto getCurrentUserDetails(User user);
    Long getCurrentUserId(User user);
}