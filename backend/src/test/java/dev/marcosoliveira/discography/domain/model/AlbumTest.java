package dev.marcosoliveira.discography.domain.model;

import dev.marcosoliveira.discography.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {

    String title = "Live in Texas";
    LocalDate releaseDate = LocalDate.of(2003,11,18);
    ImageReference cover = ImageReference.of(
            "album-cover",
            "sdghfsdhsdhsdghsdgfhsdg",
            "image/png");
    Artist linkinPark = Artist.record("Linkin Park", "Rock", ImageReference.of(
            "artist-profile-image",
            "sadfgsdafgsdfgsdfgsdfg",
            "image/png"
    ));
    Artist jayZ = Artist.record("Jay-Z", "hip-hop", ImageReference.of(
            "artist-profile-image",
            "hsdfghsdgfsdfgsdfgsdfgsdfg",
            "image/png"
    ));

    @Test
    void shouldCreateAlbum() {
        Set<Artist> artists = new HashSet<>();
        artists.add(linkinPark);

        Album album = Album.record(
                title,
                releaseDate,
                cover,
                artists
        );

        assertEquals(title, album.getTitle());
        assertEquals(releaseDate, album.getReleaseDate());
        assertEquals(cover, album.getCover());
        assertEquals(artists, album.getArtists());
    }

    @Test
    void shouldNotCreateAlbum() {
        Set<Artist> artists = new HashSet<>();
        artists.add(linkinPark);

        assertThrows(DomainException.class, () -> {
            Album album = Album.record(
                    "",
                    releaseDate,
                    cover,
                    artists
            );
        });

        assertThrows(DomainException.class, () -> {
            Album album = Album.record(
                    title,
                    null,
                    cover,
                    artists
            );
        });

        assertThrows(DomainException.class, () -> {
            Album album = Album.record(
                    title,
                    releaseDate,
                    null,
                    artists
            );
        });

        assertThrows(DomainException.class, () -> {
            Album album = Album.record(
                    title,
                    releaseDate,
                    cover,
                    null
            );
        });
    }

    @Test
    void shouldAddArtist() {
        Set<Artist> artists = new HashSet<>();
        artists.add(linkinPark);

        Album album = Album.record(
                "Collision Course",
                LocalDate.of(2004, 11, 30),
                cover,
                artists
        );

        artists.add(jayZ);
        album.addArtist(jayZ);

        assertEquals(artists, album.getArtists());
    }

    @Test
    void shouldRemoveArtist() {
        Set<Artist> artists = new HashSet<>();
        artists.add(linkinPark);
        artists.add(jayZ);

        Album album = Album.record(
                title,
                releaseDate,
                cover,
                artists
        );

        artists.remove(jayZ);
        album.removeArtist(jayZ);

        assertEquals(artists, album.getArtists());
    }

}