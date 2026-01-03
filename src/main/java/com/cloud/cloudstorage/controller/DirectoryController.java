package com.cloud.cloudstorage.controller;

import com.cloud.cloudstorage.dto.BaseResourceResponseDto;
import com.cloud.cloudstorage.dto.DirectoryResponseDto;
import com.cloud.cloudstorage.dto.ErrorResponseDto;
import com.cloud.cloudstorage.service.ResourceService;
import com.cloud.cloudstorage.validation.ValidDirectoryPath;
import com.cloud.cloudstorage.validation.ValidPath;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponses(
        {
                @ApiResponse(
                        responseCode = "500",
                        description = "Unknown exception",
                        content = @Content(
                                mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = ErrorResponseDto.class)
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
@Tag(name = "Directory API", description = "Endpoints for actions only with directories")
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequestMapping("/api/directory")
@AllArgsConstructor
public class DirectoryController {
    private final ResourceService resourceService;

    @PostMapping
    @Operation(
            summary = "Create new directory",
            description = "Creates a new directory at the specified path. The path must end with '/'",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full directories path. Must not be empty and must end with '/'",
                            example = "example-dir/example-dir2/",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Directory successfully created.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DirectoryResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Example for created directory",
                                                    value = """
                                                                {
                                                                    "path":"example-dir/",
                                                                    "name":"example-dir2",
                                                                    "type":"DIRECTORY"
                                                                }
                                                            """
                                            )

                                    }
                            )

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid path",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Parent path not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<BaseResourceResponseDto> create(
            @RequestParam
            @NotBlank(message = "Param \"path\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @ValidDirectoryPath
            String path,
            @AuthenticationPrincipal
            User user
    ) {
        BaseResourceResponseDto createDirectoryResponseDto = resourceService.createEmptyDirectory(path, user);
        return new ResponseEntity<>(createDirectoryResponseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
            summary = "Get directory content",
            description = "Returns the list of resources contained in the specified directory.",
            parameters = {
                    @Parameter(
                            name = "path",
                            description = "Full resources path. Must not be empty. If it is a directory, must end with '/'",
                            example = "example-dir/file.txt",
                            in = ParameterIn.QUERY
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Directory content received successfully.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BaseResourceResponseDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Example for file",
                                                    value = """
                                                                [
                                                                    {
                                                                        "path":"example-dir2/file.txt",
                                                                        "name":"file.txt",
                                                                        "type":"FILE",
                                                                        "size":1234
                                                                    }
                                                                ]
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Example for directory",
                                                    value = """
                                                                [
                                                                    {
                                                                        "path":"example-dir/",
                                                                        "name":"example-dir2",
                                                                        "type":"DIRECTORY"
                                                                    }
                                                                ]
                                                            """
                                            )

                                    }
                            )

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid path",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Directory not found",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }

    )
    public ResponseEntity<List<BaseResourceResponseDto>> get(
            @RequestParam
            @NotBlank(message = "Param \"path\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @ValidDirectoryPath
            String path,
            @AuthenticationPrincipal
            User user
    ) {
        List<BaseResourceResponseDto> createDirectoryResponseDto = resourceService.getDirectoryContent(path, user);
        return new ResponseEntity<>(createDirectoryResponseDto, HttpStatus.OK);
    }
}
