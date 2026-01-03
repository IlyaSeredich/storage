package com.cloud.cloudstorage.dto;

import com.cloud.cloudstorage.validation.ValidEmail;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO for user registration request")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateDto {
    @Schema(
            description = "Username of the user. Must be between 4 and 15 characters long and not blank.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Username must not be blank")
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters long")
    private String username;


    @Schema(
            description = "Password of the user. Must have at least 6 characters and be not blank.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @Schema(
            description = "Email of the user. Must be a valid email and not blank.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Email must not be blank")
    @Email
    @ValidEmail
    private String email;
}
