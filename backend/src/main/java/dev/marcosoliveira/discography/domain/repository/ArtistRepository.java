package dev.marcosoliveira.discography.domain.repository;

import dev.marcosoliveira.discography.domain.model.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Artist> findByGenreContainingIgnoreCase(String genre, Pageable pageable);

    @Query("SELECT a FROM Artist a JOIN a.albums alb WHERE alb.id = :albumId")
    Page<Artist> findByAlbumId(@Param("albumId") UUID albumId, Pageable pageable);

}
