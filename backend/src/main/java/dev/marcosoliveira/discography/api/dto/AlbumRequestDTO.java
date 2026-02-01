package dev.marcosoliveira.discography.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Data required to create a new album")
public class AlbumRequestDTO {

    @NotBlank
    @Schema(
            description = "Title of the album",
            example = "Hybrid Theory",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(
            description = "Release date of the album",
            example = "2000-10-24",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDate releaseDate;

    @NotNull
    @Schema(
            description = "List of artist IDs associated with the album",
            example = "[\"550e8400-e29b-41d4-a716-446655440000\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<UUID> artistIds;
}
