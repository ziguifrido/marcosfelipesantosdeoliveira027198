package dev.marcosoliveira.discography.api.controller;

import dev.marcosoliveira.discography.api.dto.AlbumRequestDTO;
import dev.marcosoliveira.discography.api.dto.AlbumResponseDTO;
import dev.marcosoliveira.discography.api.mapper.AlbumMapper;
import dev.marcosoliveira.discography.application.service.AlbumService;
import dev.marcosoliveira.discography.domain.model.Album;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
    }

    @GetMapping
    public ResponseEntity<Page<AlbumResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findAll(pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponseDTO> findById(@PathVariable UUID id) {
        Album album = albumService.findById(id);
        return ResponseEntity.ok(albumMapper.toResponseDto(album));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<Page<AlbumResponseDTO>> findByTitle(
            @PathVariable String title, @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByTitle(title, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/releaseDate/")
    public ResponseEntity<Page<AlbumResponseDTO>> findByReleaseDate(
                @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate after,
                @RequestParam(value = "before", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate before,
                @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByReleaseDate(after, before, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<Page<AlbumResponseDTO>> findByArtistId(
            @PathVariable UUID id, @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        Page<Album> albumPage = albumService.findByArtistId(id, pageable);
        Page<AlbumResponseDTO> responsePage = albumPage.map(albumMapper::toResponseDto);
        return ResponseEntity.ok(responsePage);
    }


    @PostMapping
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

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable UUID id,
                                            @RequestBody @Valid AlbumRequestDTO request) {
        albumService.updateAlbum(id, request.getTitle(), request.getReleaseDate(), request.getArtistIds());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/cover")
    public ResponseEntity<Void> uploadCover(
            @PathVariable UUID id, @RequestParam("file")MultipartFile file) throws IOException {
        albumService.uploadAlbumCover(id, file.getInputStream(), file.getContentType());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
