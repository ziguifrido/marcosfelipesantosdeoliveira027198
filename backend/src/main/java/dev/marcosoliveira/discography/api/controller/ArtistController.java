package dev.marcosoliveira.discography.api.controller;

import dev.marcosoliveira.discography.api.dto.ArtistRequestDTO;
import dev.marcosoliveira.discography.api.dto.ArtistResponseDTO;
import dev.marcosoliveira.discography.api.mapper.ArtistMapper;
import dev.marcosoliveira.discography.application.service.ArtistService;
import dev.marcosoliveira.discography.domain.model.Artist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    public ArtistController(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @Operation(
            summary = "Find all artists",
            description = "Returns a paginated list of all artists in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of artists",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<ArtistResponseDTO>> findAll(
            @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findAll(pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find artist by ID",
            description = "Returns a single artist by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the artist",
                    content = @Content(schema = @Schema(implementation = ArtistResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Artist not found with the given ID",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDTO> findById(@PathVariable UUID id) {
        Artist artist = artistService.findById(id);
        return ResponseEntity.ok(artistMapper.toResponseDto(artist));
    }

    @Operation(
            summary = "Find artists by name",
            description = "Returns a paginated list of artists matching the specified name."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of artists matching the name",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content)
    })
    @GetMapping("/name/{name}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByName(
            @PathVariable String name, @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByName(name, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find artists by genre",
            description = "Returns a paginated list of artists matching the specified genre."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of artists matching the genre",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content)
    })
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByGenre(
            @PathVariable String genre, @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByGenre(genre, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find artists by album ID",
            description = "Returns a paginated list of artists associated with the specified album ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of artists associated with the album",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content)
    })
    @GetMapping("/album/{id}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByAlbumId(
            @PathVariable UUID id, @ParameterObject @PageableDefault(sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByAlbumId(id, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Creates a new Artist with profile image",
            description = "This endpoint receives the name and genre of an Artist via JSON and its profile image via binary file."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artist successfully created",
                    content = @Content(schema = @Schema(implementation = ArtistResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or file format",
                    content = @Content),
            @ApiResponse(responseCode = "415", description = "Unsupported media type for the uploaded file",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestBody(
            content = @Content(
                    encoding = @Encoding(
                            name = "request",
                            contentType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    public ResponseEntity<ArtistResponseDTO> create(
            @RequestPart("request") @Valid ArtistRequestDTO request,
            @RequestPart("file") MultipartFile file) throws IOException {
        Artist artist = artistService.createArtist(
                request.getName(),
                request.getGenre()
        );

        artistService.uploadArtistProfileImage(
                artist.getId(),
                file.getInputStream(),
                file.getContentType()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(artistMapper.toResponseDto(artist));
    }

    @Operation(
            summary = "Update an existing artist",
            description = "Updates the name and genre of an existing artist identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artist successfully updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Artist not found with the given ID",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateArtist(@PathVariable UUID id,
                                             @RequestBody @Valid ArtistRequestDTO request) {
        artistService.updateArtist(id, request.getName(), request.getGenre());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

@Operation(
            summary = "Upload profile image for an artist",
            description = "Uploads a new profile image for an existing artist identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Profile image successfully uploaded",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Artist not found with the given ID",
                    content = @Content),
            @ApiResponse(responseCode = "415", description = "Unsupported media type for the uploaded file",
                    content = @Content)
    })
    @PutMapping("/{id}/profileImage")
    public ResponseEntity<Void> uploadProfileImage(
            @PathVariable UUID id, @RequestParam("file")MultipartFile file) throws IOException {
        artistService.uploadArtistProfileImage(id, file.getInputStream(), file.getContentType());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Delete an artist",
            description = "Deletes an artist from the system identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artist successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Artist not found with the given ID",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
