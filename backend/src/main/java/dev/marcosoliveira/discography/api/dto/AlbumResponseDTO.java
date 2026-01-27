package dev.marcosoliveira.discography.api.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AlbumResponseDTO {
    private UUID id;
    private String title;
    private LocalDate releaseDate;
    private String coverUrl;
    private List<String> artistNames;
}
