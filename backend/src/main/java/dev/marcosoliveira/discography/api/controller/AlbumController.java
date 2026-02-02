package dev.marcosoliveira.discography.api.controller;

import dev.marcosoliveira.discography.api.dto.AlbumRequestDTO;
import dev.marcosoliveira.discography.api.dto.AlbumResponseDTO;
import dev.marcosoliveira.discography.api.mapper.AlbumMapper;
import dev.marcosoliveira.discography.application.service.AlbumService;
import dev.marcosoliveira.discography.domain.model.Album;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Albums", description = "Endpoints for managing albums and their cover images")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
    }

    @Operation(
            summary = "Find all albums",
            description = "Returns a paginated list of all albums in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of albums",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - authentication required",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> findAll(
            @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findAll(pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find album by ID",
            description = "Returns a single album by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the album",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Album not found with the given ID",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - authentication required",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> findById(@PathVariable UUID id) {
        Album album = albumService.findById(id);
        return ResponseEntity.ok(albumMapper.toResponseDto(album));
    }

    @Operation(
            summary = "Find albums by title",
            description = "Returns a paginated list of albums matching the specified title."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of albums matching the title",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - authentication required",
                    content = @Content)
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<Page<AlbumResponseDTO>> findByTitle(
            @PathVariable String title, @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByTitle(title, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find albums by release date",
            description = "Returns a paginated list of albums filtered by release date range (after and/or before dates)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of albums within the date range",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - authentication required",
                    content = @Content)
    })
    @GetMapping("/releaseDate/")
    public ResponseEntity<Page<AlbumResponseDTO>> findByReleaseDate(
                @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByReleaseDate(after, before, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Find albums by artist ID",
            description = "Returns a paginated list of albums associated with the specified artist ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of albums associated with the artist",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - authentication required",
                    content = @Content)
    })
    @GetMapping("/artist/{id}")
    public ResponseEntity<Page<AlbumResponseDTO>> findByArtistId(
            @PathVariable UUID id, @ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByArtistId(id, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @Operation(
            summary = "Creates a new Album with cover image.",
            description = "This endpoint receives the title, release date, and artist IDs of an Album via JSON and its cover image via binary file."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Album successfully created",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or file format",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - requires ADMIN role",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "One or more artists not found with the given IDs",
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
    public ResponseEntity<AlbumResponseDTO> create(
            @RequestPart("request") @Valid AlbumRequestDTO request,
            @RequestPart("file") MultipartFile file) throws IOException {
        Album album = albumService.createAlbum(
                request.getTitle(),
                request.getReleaseDate(),
                request.getArtistIds()
        );

        albumService.uploadAlbumCover(
                album.getId(),
                file.getInputStream(),
                file.getContentType()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumMapper.toResponseDto(album));
    }

    @Operation(
            summary = "Update an existing album",
            description = "Updates the title, release date, and artist associations of an existing album identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Album successfully updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - requires ADMIN role",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Album or one or more artists not found with the given IDs",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable UUID id,
                                            @RequestBody @Valid AlbumRequestDTO request) {
        albumService.updateAlbum(id, request.getTitle(), request.getReleaseDate(), request.getArtistIds());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Upload cover image for an album",
            description = "Uploads a new cover image for an existing album identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cover image successfully uploaded",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - requires ADMIN role",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Album not found with the given ID",
                    content = @Content),
            @ApiResponse(responseCode = "415", description = "Unsupported media type for the uploaded file",
                    content = @Content)
    })
    @PutMapping(value = "/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequestBody(
            content = @Content(
                    encoding = @Encoding(
                            name = "request",
                            contentType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    public ResponseEntity<Void> uploadCover(
            @PathVariable UUID id, @RequestParam("file")MultipartFile file) throws IOException {
        albumService.uploadAlbumCover(id, file.getInputStream(), file.getContentType());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
            summary = "Delete an album",
            description = "Deletes an album from the system identified by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Album successfully deleted",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access forbidden - requires ADMIN role",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Album not found with the given ID",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
