package dev.marcosoliveira.discography.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArtistRequestDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String genre;
}
