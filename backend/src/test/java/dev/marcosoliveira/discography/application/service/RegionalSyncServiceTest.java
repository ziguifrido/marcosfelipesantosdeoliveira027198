package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.model.Regional;
import dev.marcosoliveira.discography.domain.repository.RegionalRepository;
import dev.marcosoliveira.discography.infrastructure.client.RegionalClient;
import dev.marcosoliveira.discography.infrastructure.client.RegionalExternalDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegionalSyncServiceTest {

    @Mock
    private RegionalClient regionalClient;

    @Mock
    private RegionalRepository regionalRepository;

    @InjectMocks
    private RegionalSyncService regionalSyncService;

    private Regional regional1;
    private Regional regional2;
    private RegionalExternalDTO externalRegional1;
    private RegionalExternalDTO externalRegional2;
    private RegionalExternalDTO externalRegional3;

    @BeforeEach
    void setUp() {
        regional1 = Regional.builder()
                .idInternal(UUID.randomUUID())
                .id(101)
                .nome("Regional A")
                .ativo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        regional2 = Regional.builder()
                .idInternal(UUID.randomUUID())
                .id(102)
                .nome("Regional B")
                .ativo(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        externalRegional1 = new RegionalExternalDTO(101, "Regional A");
        externalRegional2 = new RegionalExternalDTO(102, "Regional B - Changed"); // Name changed
        externalRegional3 = new RegionalExternalDTO(103, "Regional C"); // New regional
    }

    @Test
    void syncRegionals_ShouldAddNewRegional() {
        when(regionalClient.fetchAllRegionals()).thenReturn(List.of(externalRegional1, externalRegional3));
        when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(regional1));

        regionalSyncService.syncRegionals();

        ArgumentCaptor<List<Regional>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(regionalRepository, times(1)).saveAll(listCaptor.capture());

        List<Regional> savedRegionals = listCaptor.getValue();
        assertEquals(1, savedRegionals.size());
        Regional newRegional = savedRegionals.getFirst();
        assertEquals(externalRegional3.getId(), newRegional.getId());
        assertTrue(newRegional.getAtivo());
    }

    @Test
    void syncRegionals_ShouldInactivateMissingRegional() {
        when(regionalClient.fetchAllRegionals()).thenReturn(List.of(externalRegional1));
        when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(regional1, regional2));

        regionalSyncService.syncRegionals();

        ArgumentCaptor<List<Regional>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(regionalRepository, times(1)).saveAll(listCaptor.capture());

        List<Regional> savedRegionals = listCaptor.getValue();
        assertEquals(1, savedRegionals.size());
        Regional inactivatedRegional = savedRegionals.getFirst();
        assertEquals(regional2.getId(), inactivatedRegional.getId());
        assertFalse(inactivatedRegional.getAtivo());
    }

    @Test
    void syncRegionals_ShouldHandleChangedRegional() {
        when(regionalClient.fetchAllRegionals()).thenReturn(List.of(externalRegional1, externalRegional2));
        when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(regional1, regional2));

        regionalSyncService.syncRegionals();

        ArgumentCaptor<List<Regional>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(regionalRepository, times(1)).saveAll(listCaptor.capture());

        List<Regional> savedRegionals = listCaptor.getValue();
        assertEquals(2, savedRegionals.size());

        Optional<Regional> oldRegional = savedRegionals.stream().filter(r -> !r.getAtivo()).findFirst();
        assertTrue(oldRegional.isPresent());
        assertEquals(regional2.getId(), oldRegional.get().getId());

        Optional<Regional> newRegional = savedRegionals.stream().filter(Regional::getAtivo).findFirst();
        assertTrue(newRegional.isPresent());
        assertEquals(externalRegional2.getNome(), newRegional.get().getNome());
    }

    @Test
    void syncRegionals_ShouldDoNothingIfNoChanges() {
        externalRegional2 = new RegionalExternalDTO(102, "Regional B");
        when(regionalClient.fetchAllRegionals()).thenReturn(List.of(externalRegional1, externalRegional2));
        when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(regional1, regional2));

        regionalSyncService.syncRegionals();

        verify(regionalRepository, never()).saveAll(any());
    }
}
