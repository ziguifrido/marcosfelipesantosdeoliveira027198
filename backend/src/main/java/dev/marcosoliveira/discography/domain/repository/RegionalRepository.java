package dev.marcosoliveira.discography.domain.repository;

import dev.marcosoliveira.discography.domain.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, UUID> {
    List<Regional> findAllByAtivoTrue();
}
