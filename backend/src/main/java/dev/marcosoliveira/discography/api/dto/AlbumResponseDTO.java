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
            example = "http://localhost:9000/album-cover/album_123.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio-admin%2F20250201%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250201T120000Z&X-Amz-Expires=1800&X-Amz-SignedHeaders=host&X-Amz-Signature=abc123")
    private String coverUrl;

    @Schema(description = "List of artist names associated with the album", example = "[\"Linkin Park\"]")
    private List<String> artistNames;

}
