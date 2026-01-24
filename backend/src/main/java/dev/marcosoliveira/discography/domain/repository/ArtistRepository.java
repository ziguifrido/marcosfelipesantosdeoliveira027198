package dev.marcosoliveira.discography.domain.repository;

import dev.marcosoliveira.discography.domain.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, UUID> {
}
