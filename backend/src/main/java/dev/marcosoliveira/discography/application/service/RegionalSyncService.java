package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.domain.model.Regional;
import dev.marcosoliveira.discography.domain.repository.RegionalRepository;
import dev.marcosoliveira.discography.infrastructure.client.RegionalClient;
import dev.marcosoliveira.discography.infrastructure.client.RegionalExternalDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RegionalSyncService {

    private final RegionalClient regionalClient;
    private final RegionalRepository regionalRepository;

    public RegionalSyncService(RegionalClient regionalClient, RegionalRepository regionalRepository) {
        this.regionalClient = regionalClient;
        this.regionalRepository = regionalRepository;
    }

    /**
     * Synchronizes regional data from an external API with the internal database.
     * This method is designed for optimal performance by minimizing database operations and using efficient in-memory data processing.
     * The algorithmic complexity is O(N+M), where N is the number of external regionals and M is the number of active internal regionals.
     * This is the lowest possible complexity as it requires at least one pass over both datasets.
     * The implementation uses a single transaction and a batch save operation to ensure data consistency and efficiency.
     */
    @Transactional
    public String syncRegionals() {
        List<RegionalExternalDTO> externalRegionals = regionalClient.fetchAllRegionals();
        return performSync(externalRegionals);
    }

    private String performSync(List<RegionalExternalDTO> externalRegionals) {
        Map<Integer, RegionalExternalDTO> externalRegionalsMap = externalRegionals.stream()
                .collect(Collectors.toMap(RegionalExternalDTO::getId, Function.identity()));
        Map<Integer, Regional> internalRegionalsMap = regionalRepository.findAllByAtivoTrue().stream()
                .collect(Collectors.toMap(Regional::getId, Function.identity()));

        Set<Integer> externalIds = externalRegionalsMap.keySet();
        Set<Integer> internalIds = internalRegionalsMap.keySet();

        Set<Integer> newIds = new HashSet<>(externalIds);
        newIds.removeAll(internalIds);

        Set<Integer> missingIds = new HashSet<>(internalIds);
        missingIds.removeAll(externalIds);

        Set<Integer> commonIds = new HashSet<>(internalIds);
        commonIds.retainAll(externalIds);

        List<Regional> regionalsToSave = new ArrayList<>();

        // Process new regionals
        for (Integer id : newIds) {
            regionalsToSave.add(createNewRegional(externalRegionalsMap.get(id)));
        }

        // Process missing regionals
        for (Integer id : missingIds) {
            Regional regional = internalRegionalsMap.get(id);
            regional.deactivate();
            regionalsToSave.add(regional);
        }

        // Process common regionals for changes
        long changedCount = 0;
        for (Integer id : commonIds) {
            Regional internalRegional = internalRegionalsMap.get(id);
            RegionalExternalDTO externalRegional = externalRegionalsMap.get(id);
            if (!internalRegional.getNome().equals(externalRegional.getNome())) {
                internalRegional.deactivate();
                regionalsToSave.add(internalRegional);
                regionalsToSave.add(createNewRegional(externalRegional));
                changedCount++;
            }
        }

        if (!regionalsToSave.isEmpty()) {
            regionalRepository.saveAll(regionalsToSave);
        }

        return String.format("Regional synchronization process completed. New: %d, Inactivated: %d, Changed: %d",
                newIds.size(),
                missingIds.size(),
                changedCount
        );
    }

    private Regional createNewRegional(RegionalExternalDTO externalRegional) {
        return Regional.builder()
                .id(externalRegional.getId())
                .nome(externalRegional.getNome())
                .ativo(true)
                .build();
    }
}
