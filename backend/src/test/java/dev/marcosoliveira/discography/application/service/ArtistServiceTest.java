package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Artist;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import dev.marcosoliveira.discography.domain.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> artistService.findById(id));
        verify(artistRepository).findById(id);
    }

    @Test
    void createArtist_ShouldReturnArtist() {
        String name = "Linkin Park";
        String genre = "Rock";

        when(artistRepository.saveAndFlush(any(Artist.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        Artist artist = artistService.createArtist(name, genre);

        assertNotNull(artist);
        assertEquals(name, artist.getName());
        assertEquals(genre, artist.getGenre());
        verify(artistRepository, times(1)).saveAndFlush(any(Artist.class));
    }

    @Test
    void uploadArtistProfileImage_ShouldSucceed() {
        UUID artistId = UUID.randomUUID();
        Artist artist = Artist.record("Linkin Park", "Rock");
        InputStream content = new ByteArrayInputStream("image-data".getBytes());
        String contentType = "image/jpeg";
        ImageReference imgRef = new ImageReference("bucket", "key", contentType);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(storageService.uploadArtistProfileImage(eq(artistId), any(), eq(contentType))).thenReturn(imgRef);
        when(artistRepository.saveAndFlush(any(Artist.class))).thenReturn(artist);

        artistService.uploadArtistProfileImage(artistId, content, contentType);

        assertNotNull(artist.getProfileImage());
        assertEquals("key", artist.getProfileImage().getObjectKey());
        verify(storageService).uploadArtistProfileImage(eq(artistId), any(), eq(contentType));
        verify(artistRepository).saveAndFlush(artist);
    }

    @Test
    void uploadArtistProfileImage_WithInvalidId_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                artistService.uploadArtistProfileImage(id, null, "image/png")
        );
    }
}