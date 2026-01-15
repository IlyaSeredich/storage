package com.cloud.cloudstorage.controller;

import com.cloud.cloudstorage.dto.UserAuthDto;
import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.UserResponseDto;
import com.cloud.cloudstorage.service.CurrentUserService;
import com.cloud.cloudstorage.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@ApiResponses(
        {
                @ApiResponse(responseCode = "400", description = "Invalid request data"),
                @ApiResponse(responseCode = "500", description = "Unknown error")
        }
)
@Tag(name = "User API", description = "Endpoints for managing authentication and user data")
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final CurrentUserService currentUserService;
    private final UserAccountService userAccountService;

    @Operation(
            summary = "Register new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered"),
                    @ApiResponse(responseCode = "409", description = "Username already exists")
            }
    )
    @PostMapping("/auth/sign-up")
    public ResponseEntity<UserResponseDto> register(
            @Valid @RequestBody UserCreateDto userCreateDto,
            HttpServletRequest request) {
        UserResponseDto userResponseDto = userAccountService.registerNewUser(userCreateDto, request);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Authorize user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully authorized"),
                    @ApiResponse(responseCode = "401", description = "Wrong username or password")
            }
    )
    @PostMapping("/auth/sign-in")
    public ResponseEntity<UserResponseDto> authorize(
            @Valid @RequestBody UserAuthDto userAuthDto,
            HttpServletRequest request) {
        UserResponseDto userResponseDto = userAccountService.authorizeUser(userAuthDto, request);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "User logout",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Logout successful. Session destroyed and cookies cleared."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user.")
            }
    )
    @PostMapping("/auth/sign-out")
    public void logout() {
    }

    @Operation(
            summary = "Get current user details",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users details successfully received"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized user")
            }
    )
    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal User user) {
        UserResponseDto userResponseDto = currentUserService.getCurrentUserDetails(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
}
