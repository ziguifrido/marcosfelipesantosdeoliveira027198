package dev.marcosoliveira.discography.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Artist details returned by the API")
public class ArtistResponseDTO {

    @Schema(description = "Unique identifier of the artist", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Artistic name", example = "Linkin Park")
    private String name;

    @Schema(description = "Musical genre", example = "Rock")
    private String genre;

    @Schema(description = "URL of the profile image",
            example = "http://localhost:9000/artist-profile-image/artist_123.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio-admin%2F20250201%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250201T120000Z&X-Amz-Expires=1800&X-Amz-SignedHeaders=host&X-Amz-Signature=abc123")
    private String profileImageUrl;

    @Schema(description = "List of main album titles", example = "[\"Live in Texas\", \"Hybrid Theory\", \"Meteora\"]")
    private List<String> albumTitles;

}
