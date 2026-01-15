package com.cloud.cloudstorage.controller;

import com.cloud.cloudstorage.dto.ErrorResponseDto;
import com.cloud.cloudstorage.dto.UserAuthDto;
import com.cloud.cloudstorage.dto.UserCreateDto;
import com.cloud.cloudstorage.dto.UserResponseDto;
import com.cloud.cloudstorage.service.CurrentUserService;
import com.cloud.cloudstorage.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@ApiResponses(
        @ApiResponse(
                responseCode = "500",
                description = "Unknown error",
                content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponseDto.class)
                )
        )
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for new user registration",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserCreateDto.class),
                            examples = @ExampleObject(
                                    name = "Example data for registration",
                                    value = """
                                            {
                                                "username":"testUser",
                                                "password":"testPassword",
                                                "email":"test-email@gmail.com"
                                            }
                                            """

                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully registered",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Username already taken",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping("/auth/sign-up")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserCreateDto userCreateDto,
                                                    HttpServletRequest request) {
        UserResponseDto userResponseDto = userAccountService.registerNewUser(userCreateDto, request);
        return new ResponseEntity<>(userResponseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Authorize user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for user authorization",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserAuthDto.class),
                            examples = @ExampleObject(
                                    name = "Example data for authorization",
                                    value = """
                                            {
                                                "username":"testUser",
                                                "password":"testPassword"
                                            }
                                            """

                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully authorized",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Wrong username or password",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
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
            description = "Invalidates current session and clears cookies. Requires valid authentication.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Logout successful. Session destroyed and cookies cleared."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized user.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping("/auth/sign-out")
    public void logout() {
    }

    @Operation(
            summary = "Get current user details",
            security = @SecurityRequirement(name = "cookieAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Users details successfully received",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )

                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized user",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/user/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal User user) {
        UserResponseDto userResponseDto = currentUserService.getCurrentUserDetails(user);
        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }
}
