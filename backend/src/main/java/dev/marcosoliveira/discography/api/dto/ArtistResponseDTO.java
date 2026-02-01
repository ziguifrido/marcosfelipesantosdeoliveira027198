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
            example = "http://localhost:9000/artist-profile-image/artist_a102be74-e9e2-4880-830e-b88cdb3c8b37_1769909528493")
    private String profileImageUrl;

    @Schema(description = "List of main album titles", example = "[\"Live in Texas\", \"Hybrid Theory\", \"Meteora\"]")
    private List<String> albumTitles;
}
