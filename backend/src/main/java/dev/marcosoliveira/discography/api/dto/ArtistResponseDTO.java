package dev.marcosoliveira.discography.api.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ArtistResponseDTO {
    private UUID id;
    private String name;
    private String genre;
    private String profileImageUrl;
    private List<String> albumTitles;
}
