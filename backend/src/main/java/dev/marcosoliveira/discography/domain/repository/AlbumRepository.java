package dev.marcosoliveira.discography.domain.repository;

import dev.marcosoliveira.discography.domain.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlbumRepository extends JpaRepository<Album, UUID> {
}
