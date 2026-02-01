package dev.marcosoliveira.discography.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Data required to create a new artist")
public class ArtistRequestDTO {

    @NotBlank
    @Schema(
            description = "Artistic name of the singer or band",
            example = "Linkin Park",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    @NotBlank
    @Schema(
            description = "Primary musical genre",
            example = "Rock",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String genre;
}
