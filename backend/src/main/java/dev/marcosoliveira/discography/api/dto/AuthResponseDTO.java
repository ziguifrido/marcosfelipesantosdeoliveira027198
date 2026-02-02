package dev.marcosoliveira.discography.api.dto;

import dev.marcosoliveira.discography.domain.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response with access and refresh tokens")
public class AuthResponseDTO {

    @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Username of the authenticated user", example = "john_doe")
    private String username;

    @Schema(description = "Role of the user", example = "USER")
    private Role role;

    @Schema(description = "JWT access token (expires in 5 minutes)", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token (expires in 24 hours)", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Access token expiration time in seconds", example = "300")
    private Long expiresIn;

}
