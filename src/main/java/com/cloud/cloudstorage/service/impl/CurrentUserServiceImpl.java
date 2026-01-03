package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.dto.UserResponseDto;
import com.cloud.cloudstorage.mapper.UserMapper;
import com.cloud.cloudstorage.model.User;
import com.cloud.cloudstorage.repository.UserRepository;
import com.cloud.cloudstorage.service.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getCurrentUserDetails(org.springframework.security.core.userdetails.User user) {
        String username = user.getUsername();
        return userMapper.toDto(username);
    }

    @Override
    public Long getCurrentUserId(org.springframework.security.core.userdetails.User user) {
        String username = user.getUsername();
        User foundUser = getUserByUsername(username);

        return foundUser.getId();
    }

    private User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }


}
