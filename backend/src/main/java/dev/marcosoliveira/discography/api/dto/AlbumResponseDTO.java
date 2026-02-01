package dev.marcosoliveira.discography.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Album details returned by the API")
public class AlbumResponseDTO {

    @Schema(description = "Unique identifier of the album", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "Title of the album", example = "Hybrid Theory")
    private String title;

    @Schema(description = "Release date of the album", example = "2000-10-24")
    private LocalDate releaseDate;

    @Schema(description = "URL of the cover image",
            example = "http://localhost:9000/album-cover/album_b12c8456-f3a1-48e5-9c2d-123456789abc_1769909528493")
    private String coverUrl;

    @Schema(description = "List of artist names associated with the album", example = "[\"Linkin Park\"]")
    private List<String> artistNames;
}
