package dev.marcosoliveira.discography.domain.model;

import dev.marcosoliveira.discography.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTest {

    String name = "Linking Park";
    String genre = "Rock";
    ImageReference profileImage = ImageReference.of(
            "artist-profile-image",
            "sdghfsdhsdhsdghsdgfhsdg",
            "image/png");

    @Test
    void shouldCreateArtist() {
        Artist artist = Artist.record(
                name,
                genre
        );

        artist.uploadImage(profileImage);

        assertEquals(name, artist.getName());
        assertEquals(genre, artist.getGenre());
        assertEquals(profileImage, artist.getProfileImage());

    }

    @Test
    void shouldNotCreateArtist() {
        assertThrows(DomainException.class, () -> {
            Artist.record(
                    "",
                    genre
            );
        });

        assertThrows(DomainException.class, () -> {
            Artist.record(
                    name,
                    ""
            );
        });

    }


}