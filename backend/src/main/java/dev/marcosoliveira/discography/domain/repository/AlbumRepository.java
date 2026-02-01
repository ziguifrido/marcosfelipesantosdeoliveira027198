package dev.marcosoliveira.discography.domain.repository;

import dev.marcosoliveira.discography.domain.model.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {

    Page<Album> findByTitleContainsIgnoreCase(String title, Pageable pageable);

    Page<Album> findByReleaseDateBefore(LocalDate releaseDateAfter, Pageable pageable);

    Page<Album> findByReleaseDateAfter(LocalDate releaseDateAfter, Pageable pageable);

    Page<Album> findByReleaseDateBetween(LocalDate releaseDateAfter, LocalDate releaseDateBefore, Pageable pageable);

    @Query("SELECT alb FROM Album alb JOIN alb.artists a WHERE a.id = :artistId")
    Page<Album> findByArtistId(@Param("artistId") UUID artirstId, Pageable pageable);
}
