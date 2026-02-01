package com.cloud.cloudstorage.controller;

import com.cloud.cloudstorage.dto.BaseResourceResponseDto;
import com.cloud.cloudstorage.service.ResourceService;
import com.cloud.cloudstorage.validation.ValidDirectoryPath;
import com.cloud.cloudstorage.validation.ValidPath;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiResponses(
        {
                @ApiResponse(responseCode = "500", description = "Unknown exception"),
                @ApiResponse(responseCode = "401", description = "Unauthorized user")
        }
)
@Tag(name = "Directory API", description = "Endpoints for actions only with directories")
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequestMapping("/api/directory")
@AllArgsConstructor
public class DirectoryController {
    private final ResourceService resourceService;

    @Operation(
            summary = "Create new directory",
            parameters = {
                    @Parameter(name = "path", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Directory successfully created."),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Parent path not found")
            }
    )
    @PostMapping
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

    @Operation(
            summary = "Get directory content",
            parameters = {@Parameter(name = "path", in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Directory content received successfully."),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Directory not found")
            }

    )
    @GetMapping
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
