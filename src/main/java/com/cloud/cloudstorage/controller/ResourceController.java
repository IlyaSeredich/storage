package com.cloud.cloudstorage.controller;

import com.cloud.cloudstorage.dto.BaseResourceResponseDto;
import com.cloud.cloudstorage.dto.FileResponseDto;
import com.cloud.cloudstorage.dto.FileUploadDto;
import com.cloud.cloudstorage.dto.StreamResourceDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@ApiResponses(
        {
                @ApiResponse(responseCode = "500", description = "Unknown exception"),
                @ApiResponse(responseCode = "401", description = "Unauthorized user")
        }
)
@Tag(name = "Resource API", description = "Endpoints for general actions with files and directories")
@SecurityRequirement(name = "cookieAuth")
@RestController
@RequestMapping("/api/resource")
@Validated
@AllArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @Operation(
            summary = "Upload resource",
            parameters = { @Parameter(name = "path", required = true, in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Uploading completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "409", description = "Resource already exists")
            }
    )
    @PostMapping
    public ResponseEntity<List<FileResponseDto>> upload(
            @RequestParam
            @NotBlank(message = "Param \"path\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @ValidDirectoryPath
            String path,
            @ModelAttribute
            FileUploadDto fileUploadDto,
            @AuthenticationPrincipal
            User user
    ) {
        List<FileResponseDto> responseDto = resourceService.uploadFiles(path, fileUploadDto, user);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Search resource",
            parameters = {@Parameter(name = "query", required = true, in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Searching completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid request data")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<BaseResourceResponseDto>> search(
            @NotBlank(message = "Param \"query\" should not be empty")
            @RequestParam String query,
            @AuthenticationPrincipal User user
    ) {
        List<BaseResourceResponseDto> searchedContent = resourceService.getSearchedContent(query, user);
        return new ResponseEntity<>(searchedContent, HttpStatus.OK);
    }

    @Operation(
            summary = "Move/rename resource",
            description = "Moves or renames a resource. Resource types must match: file can't be renamed to directory and vice versa.",
            parameters = {
                    @Parameter(name = "from", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "to", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Moving/renaming completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                    @ApiResponse(responseCode = "409", description = "Resource with target path 'to' already exists")
            }
    )
    @GetMapping("/move")
    public ResponseEntity<BaseResourceResponseDto> move(
            @NotBlank(message = "Param \"from\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @RequestParam
            String from,
            @NotBlank(message = "Param \"to\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @RequestParam
            String to,
            @AuthenticationPrincipal
            User user
    ) {
        BaseResourceResponseDto baseResourceResponseDto = resourceService.moveResource(from, to, user);
        return new ResponseEntity<>(baseResourceResponseDto, HttpStatus.OK);
    }

    @Operation(
            summary = "Download resource",
            description = "Downloads a file from the server. Returns binary content with Content-Disposition: attachment.",
            parameters = {
                    @Parameter(name = "path", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Downloading completed"),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download(
            @NotBlank(message = "Param \"path\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            @RequestParam
            String path,
            @AuthenticationPrincipal
            User user
    ) {
        StreamResourceDto streamResourceDto = resourceService.downloadResource(path, user);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + streamResourceDto.filename() + "\"")
                .body(streamResourceDto.body());
    }

    @Operation(
            summary = "Delete resource",
            parameters = {@Parameter(name = "path", in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Resource deleted successfully. No content is returned."),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam
            @NotBlank(message = "Param \"path\" should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            String path,
            @AuthenticationPrincipal
            User user
    ) {
        resourceService.deleteResource(path, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get resources details",
            parameters = {@Parameter(name = "path", in = ParameterIn.QUERY)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resource details received successfully."),
                    @ApiResponse(responseCode = "400", description = "Invalid path"),
                    @ApiResponse(responseCode = "404", description = "Resource not found")
            }
    )
    @GetMapping
    public ResponseEntity<BaseResourceResponseDto> get(
            @RequestParam
            @NotBlank(message = "Param 'path' should not be empty")
            @ValidPath(message = "Incorrect character in path: \\")
            String path,
            @AuthenticationPrincipal
            User user
    ) {
        BaseResourceResponseDto baseResourceResponseDto = resourceService.getResourceInfo(path, user);
        return new ResponseEntity<>(baseResourceResponseDto, HttpStatus.OK);
    }
}
