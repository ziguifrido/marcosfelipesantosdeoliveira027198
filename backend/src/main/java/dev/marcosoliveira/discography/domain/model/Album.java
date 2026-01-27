package dev.marcosoliveira.discography.domain.model;

import dev.marcosoliveira.discography.domain.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter(AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Album extends AbstractAggregateRoot<Album> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "bucket", column = @Column(name = "cover_bucket")),
            @AttributeOverride(name = "objectKey", column = @Column(name = "cover_object_key")),
            @AttributeOverride(name = "contentType", column = @Column(name = "cover_content_type"))
    })
    private ImageReference cover;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "album_artists",
            joinColumns = @JoinColumn(name = "album_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    @Builder.Default
    private Set<Artist> artists = new HashSet<>();

    public static Album record(String title, LocalDate releaseDate, Set<Artist> artists) {
        validate(title, releaseDate, artists);
        return Album.builder()
                .title(title)
                .releaseDate(releaseDate)
                .artists(artists)
                .build();
    }

    public void update(String title, LocalDate releaseDate) {
        validate(title, releaseDate);
        setTitle(title);
        setReleaseDate(releaseDate);
    }

    public void uploadImage(ImageReference cover){
        validate(cover);
        setCover(cover);
    }

    public void addArtist(Artist artist) {
        getArtists().add(artist);
        artist.getAlbums().add(this);
    }

    public void removeArtist(Artist artist) {
        getArtists().remove(artist);
        artist.getAlbums().remove(this);
    }

    private static void validate(String title, LocalDate releaseDate) {
        if (title == null || title.isBlank()) throw new DomainException("Title cannot be empty!");
        if (releaseDate == null) throw new DomainException("Release Date cannot be empty!");
        if (releaseDate.isAfter(LocalDate.now())) throw new DomainException("Release date cannot be in the future!");
    }

    private static void validate(Set<Artist> artists) {
        if (artists == null || artists.isEmpty()) throw new DomainException("Album must have at least one Artist!");
    }

    private static void validate(ImageReference cover) {
        if (cover == null) throw new DomainException("Album Cover cannot be empty!");
    }

    private static void validate(String title, LocalDate releaseDate, Set<Artist> artists) {
        validate(title, releaseDate);
        validate(artists);
    }

}
