package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Artist;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import dev.marcosoliveira.discography.domain.repository.AlbumRepository;
import dev.marcosoliveira.discography.domain.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final StorageService storageService;

    public ArtistService(ArtistRepository artistRepository,
                         AlbumRepository albumRepository, StorageService storageService) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.storageService = storageService;
    }

    public Page<Artist> findAll(Pageable pageable) { return artistRepository.findAll(pageable); }

    public Artist findById(UUID artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));
    }

    public Page<Artist> findByName(String name, Pageable pageable) {
        return artistRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Artist> findByGenre(String genre, Pageable pageable) {
        return artistRepository.findByGenreContainingIgnoreCase(genre, pageable);
    }

    public Page<Artist> findByAlbumId(UUID albumId, Pageable pageable) {
        return artistRepository.findByAlbumId(albumId, pageable);
    }

    @Transactional
    public Artist createArtist(String name, String genre) {
        Artist artist = Artist.record(name, genre);
        return artistRepository.saveAndFlush(artist);
    }

    @Transactional
    public void uploadArtistProfileImage(UUID artistId, InputStream content, String contentType) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));

        if (artist.getProfileImage() != null)
            storageService.delete(artist.getProfileImage());

        ImageReference profileImage = storageService.uploadArtistProfileImage(artistId, content, contentType);

        artist.uploadImage(profileImage);

        artistRepository.saveAndFlush(artist);
    }

    @Transactional
    public void updateArtist(UUID artistId, String name, String genre) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));

        artist.update(name, genre);

        artistRepository.saveAndFlush(artist);
    }

    @Transactional
    public void deleteArtist(UUID artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist", artistId));

        if (artist.getProfileImage() != null)
            storageService.delete(artist.getProfileImage());

        artist.getAlbums().forEach(album -> album.removeArtist(artist));

        artist.getAlbums().forEach(album -> {
            if (album.getArtists().isEmpty()) {
                if (album.getCover() != null)
                    storageService.delete(album.getCover());
                albumRepository.delete(album);
            }
        });

        artistRepository.delete(artist);
    }
}
