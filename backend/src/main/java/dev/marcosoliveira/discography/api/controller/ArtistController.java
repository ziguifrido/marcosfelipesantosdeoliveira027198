package dev.marcosoliveira.discography.api.controller;

import dev.marcosoliveira.discography.api.dto.ArtistRequestDTO;
import dev.marcosoliveira.discography.api.dto.ArtistResponseDTO;
import dev.marcosoliveira.discography.api.mapper.ArtistMapper;
import dev.marcosoliveira.discography.application.service.ArtistService;
import dev.marcosoliveira.discography.domain.model.Artist;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<Page<ArtistResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findAll(pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDTO> findById(@PathVariable UUID id) {
        Artist artist = artistService.findById(id);
        return ResponseEntity.ok(artistMapper.toResponseDto(artist));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByName(
            @PathVariable String name, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByName(name, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByGenre(
            @PathVariable String genre, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByGenre(genre, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<Page<ArtistResponseDTO>> findByAlbumId(
            @PathVariable UUID id, @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<Artist> artistPage = artistService.findByAlbumId(id, pageable);
        Page<ArtistResponseDTO> responsePage = artistPage.map(artistMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @PostMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateArtist(@PathVariable UUID id,
                                             @RequestBody @Valid ArtistRequestDTO request) {
        artistService.updateArtist(id, request.getName(), request.getGenre());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/profileImage")
    public ResponseEntity<Void> uploadProfileImage(
            @PathVariable UUID id, @RequestParam("file")MultipartFile file) throws IOException {
        artistService.uploadArtistProfileImage(id, file.getInputStream(), file.getContentType());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        artistService.deleteArtist(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
