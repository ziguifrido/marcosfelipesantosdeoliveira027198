package dev.marcosoliveira.discography.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Refresh token request to generate new access token")
public class RefreshRequestDTO {

    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token to generate new access token", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

}
