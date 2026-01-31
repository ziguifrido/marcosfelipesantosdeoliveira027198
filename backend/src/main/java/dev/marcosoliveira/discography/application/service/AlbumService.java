package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Album;
import dev.marcosoliveira.discography.domain.model.Artist;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import dev.marcosoliveira.discography.domain.repository.AlbumRepository;
import dev.marcosoliveira.discography.domain.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AlbumService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final StorageService storageService;

    public AlbumService(ArtistRepository artistRepository,
                              AlbumRepository albumRepository, StorageService storageService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.storageService = storageService;
    }

    public Page<Album> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable);
    }

    public Album findById(UUID albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));
    }

    public Page<Album> findByTitle(String title, Pageable pageable) {
        return albumRepository.findByTitleContainsIgnoreCase(title, pageable);
    }

    public Page<Album> findByReleaseDate(LocalDate after, LocalDate before, Pageable pageable) {
        if (after == null && before == null)
            return Page.empty(pageable);

        if (after == null)
            return albumRepository.findByReleaseDateBefore(before, pageable);

        if (before == null)
            return albumRepository.findByReleaseDateAfter(after, pageable);

        return albumRepository.findByReleaseDateBetween(after, before, pageable);
    }

    public Page<Album> findByArtistId(UUID artistId, Pageable pageable) {
        return albumRepository.findByArtistId(artistId, pageable);
    }

    @Transactional
    public Album createAlbum(String title, LocalDate releaseDate, List<UUID> artistIds) {
        List<Artist> artistList = artistRepository.findAllById(artistIds);
        Set<Artist> artists = new HashSet<>(artistList);

        if (artists.isEmpty())
            throw new RuntimeException("At least one valid artist is required for an album creation!");

        Album album = Album.record(title, releaseDate, artists);

        artists.forEach(artist -> artist.getAlbums().add(album));

        artistRepository.saveAll(artists);

        return albumRepository.saveAndFlush(album);
    }

    @Transactional
    public void uploadAlbumCover(UUID albumId, InputStream content, String contentType) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));

        if (album.getCover() != null)
            storageService.delete(album.getCover());

        ImageReference cover = storageService.uploadAlbumCover(albumId, content, contentType);

        album.uploadImage(cover);

        albumRepository.saveAndFlush(album);
    }

    @Transactional
    public void updateAlbum(UUID albumId, String title, LocalDate releaseDate, List<UUID> artistIds) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));

        album.update(title, releaseDate);

        List<Artist> artists = artistRepository.findAllById(artistIds);

        album.getArtists().stream().filter(
                artist -> !artists.contains(artist)
        ).forEach(album::removeArtist);

        artists.stream().filter(
                artist -> !album.getArtists().contains(artist)
        ).forEach(album::addArtist);

        albumRepository.saveAndFlush(album);
    }


    @Transactional
    public void deleteAlbum(UUID albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album", albumId));

        if (album.getCover() != null)
            storageService.delete(album.getCover());

        album.getArtists().forEach(artist -> artist.getAlbums().remove(album));

        albumRepository.delete(album);
    }
}
