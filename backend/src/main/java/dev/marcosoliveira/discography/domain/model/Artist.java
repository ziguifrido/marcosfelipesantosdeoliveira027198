package dev.marcosoliveira.discography.domain.model;

import dev.marcosoliveira.discography.domain.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.domain.AbstractAggregateRoot;

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
public class Artist extends AbstractAggregateRoot<Artist> {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String genre;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "bucket", column = @Column(name = "profile_image_bucket")),
            @AttributeOverride(name = "objectKey", column = @Column(name = "profile_image_object_key")),
            @AttributeOverride(name = "contentType", column = @Column(name = "profile_image_content_type"))
    })
    private ImageReference profileImage;

    @ManyToMany(mappedBy = "artists")
    @Builder.Default
    private Set<Album> albums = new HashSet<>();

    public static Artist record(String name, String genre, ImageReference profileImage) {
        validate(name, genre, profileImage);
        return Artist.builder()
                .name(name)
                .genre(genre)
                .profileImage(profileImage)
                .build();
    }

    public void update(String name, String genre) {
        validate(name, genre);
        setName(name);
        setGenre(genre);
    }

    public void updateImage(ImageReference profileImage){
        validate(profileImage);
        setProfileImage(profileImage);
    }

    private static void validate(String name, String genre) {
        if (name == null || name.isBlank()) throw new DomainException("Name cannot be empty!");
        if (genre == null || genre.isBlank()) throw new DomainException("Genre cannot be empty!");
    }

    private static void validate(ImageReference profileImage) {
        if (profileImage == null) throw new DomainException("Profile Image cannot be empty!");
    }

    private static void validate(String name, String genre, ImageReference profileImage) {
        validate(name, genre);
        validate(profileImage);
    }

}
