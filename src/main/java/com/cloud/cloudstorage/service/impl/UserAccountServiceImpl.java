package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.dto.UserAuthDto;
import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.UserResponseDto;
import com.cloud.cloudstorage.exception.EmailAlreadyExistException;
import com.cloud.cloudstorage.exception.UserAlreadyExistException;
import com.cloud.cloudstorage.mapper.UserMapper;
import com.cloud.cloudstorage.model.Role;
import com.cloud.cloudstorage.model.User;
import com.cloud.cloudstorage.repository.UserRepository;
import com.cloud.cloudstorage.service.AuthenticationService;
import com.cloud.cloudstorage.service.ResourceService;
import com.cloud.cloudstorage.service.RoleService;
import com.cloud.cloudstorage.service.UserAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AuthenticationService authenticationService;
    private final ResourceService resourceService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto registerNewUser(UserCreateDto userCreateDto, HttpServletRequest request) {
        String username = userCreateDto.getUsername();
        String email = userCreateDto.getEmail();
        String password = userCreateDto.getPassword();

        validateRegistrationConditions(username, email);
        User user = createUser(userCreateDto);
        authenticate(username, password, request);
        resourceService.createRootDirectory(user.getId());
        return userMapper.toDto(username);
    }

    @Override
    public UserResponseDto authorizeUser(UserAuthDto userAuthDto, HttpServletRequest request) {
        String username = userAuthDto.getUsername();
        String password = userAuthDto.getPassword();

        authenticate(username, password, request);
        return userMapper.toDto(username);
    }

    private void validateRegistrationConditions(String username, String email) {
        validateUsernameNotExists(username);
        validateEmailNotExists(email);
    }

    private void authenticate(String username, String password, HttpServletRequest request) {
        authenticationService.authenticate(username, password, request);
    }

    private User createUser(UserCreateDto userCreateDto) {
        String password = passwordEncoder.encode(userCreateDto.getPassword());
        List<Role> roles = List.of(roleService.getDefaultRole());
        User user = userMapper.toUser(userCreateDto, password, roles);
        return userRepository.save(user);
    }

    private void validateUsernameNotExists(String username) {
        if (userExists(username)) {
            throw new UserAlreadyExistException(username);
        }
    }

    private void validateEmailNotExists(String email) {
        if (emailExists(email)) {
            throw new EmailAlreadyExistException(email);
        }
    }

    private boolean userExists(String username) {
        return userRepository.existsUserByUsername(username);
    }

    private boolean emailExists(String email) {
        return userRepository.existsUserByEmail(email);
    }
}
