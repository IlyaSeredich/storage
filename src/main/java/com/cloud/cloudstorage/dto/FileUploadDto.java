package com.cloud.cloudstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Schema(description = "DTO for uploading resources")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileUploadDto {

    @Schema(
            description = "Uploading resource. Must not be empty",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty(message = "Uploading resource must not be empty")
    private List<MultipartFile> multipartFile;
}
