package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.event.AlbumCreatedEvent;
import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Album;
import dev.marcosoliveira.discography.domain.model.Artist;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import dev.marcosoliveira.discography.domain.repository.AlbumRepository;
import dev.marcosoliveira.discography.domain.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> albumService.findById(id));
        verify(albumRepository).findById(id);
    }

    @Test
    void createAlbum_WithValidArtists_ShouldReturnAlbum() {
        UUID artistId = UUID.randomUUID();
        String title = "Live in Texas";
        LocalDate releaseDate = LocalDate.of(2003, 11, 18);

        Artist artist = Artist.record("Linkin Park", "Rock");
        when(artistRepository.findAllById(any())).thenReturn(List.of(artist));
        when(albumRepository.saveAndFlush(any(Album.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Album album = albumService.createAlbum(title, releaseDate, List.of(artistId));

        assertNotNull(album);
        assertEquals(title, album.getTitle());
        assertEquals(1, album.getArtists().size());
        verify(albumRepository, times(1)).saveAndFlush(any(Album.class));
    }

    @Test
    void uploadCover_ShouldSucceed() {
        UUID artistId = UUID.randomUUID();
        String title = "Live in Texas";
        LocalDate releaseDate = LocalDate.of(2003, 11, 18);
        Artist artist = Artist.record("Linkin Park", "Rock");

        UUID albumId = UUID.randomUUID();
        InputStream content = new ByteArrayInputStream("image-data".getBytes());
        String contentType = "image/jpeg";
        ImageReference imgRef = new ImageReference("bucket", "key", contentType);

        when(artistRepository.findAllById(any())).thenReturn(List.of(artist));
        when(albumRepository.saveAndFlush(any(Album.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Album album = albumService.createAlbum(title, releaseDate, List.of(artistId));

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(storageService.uploadAlbumCover(eq(albumId), any(), eq(contentType))).thenReturn(imgRef);

        albumService.uploadAlbumCover(albumId, content, contentType);

        assertNotNull(album.getCover());
        assertEquals("key", album.getCover().getObjectKey());
        verify(storageService).uploadAlbumCover(eq(albumId), any(), eq(contentType));
        verify(albumRepository, times(2)).saveAndFlush(album);
    }

    @Test
    void uploadCover_WithInvalidId_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                albumService.uploadAlbumCover(id, null, "image/png")
        );
    }

    @Test
    void createAlbum_ShouldPublishAlbumCreatedEvent() {
        UUID artistId = UUID.randomUUID();
        String title = "Live in Texas";
        LocalDate releaseDate = LocalDate.of(2003, 11, 18);

        Artist artist = Artist.record("Linkin Park", "Rock");
        when(artistRepository.findAllById(any())).thenReturn(List.of(artist));
        when(albumRepository.saveAndFlush(any(Album.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Album album = albumService.createAlbum(title, releaseDate, List.of(artistId));

        ArgumentCaptor<AlbumCreatedEvent> eventCaptor = ArgumentCaptor.forClass(AlbumCreatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        AlbumCreatedEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(album, capturedEvent.getAlbum());
    }

}