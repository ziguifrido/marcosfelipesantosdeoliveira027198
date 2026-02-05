package dev.marcosoliveira.discography.api.controller;

import dev.marcosoliveira.discography.application.service.RegionalSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/regionals")
@Tag(name = "Regional Management", description = "Endpoints for managing regional data, including synchronization with external sources.")
public class RegionalController {

    private final RegionalSyncService regionalSyncService;

    public RegionalController(RegionalSyncService regionalSyncService) {
        this.regionalSyncService = regionalSyncService;
    }

    /**
     * Triggers the synchronization of regional data from an external API.
     * This operation fetches the latest regional data and updates the internal database
     * according to predefined business rules (new records, missing records, changed records).
     *
     * @return A ResponseEntity indicating the success or failure of the synchronization process.
     */
    @Operation(summary = "Trigger regional data synchronization",
               description = "Initiates a synchronization process to update regional data from an external API. " +
                             "New records will be inserted, missing active records will be deactivated, " +
                             "and changed records will result in the deactivation of the old record and creation of a new one.",
               responses = {
                   @ApiResponse(responseCode = "200", description = "Regional synchronization process completed",
                           content = @Content),
                   @ApiResponse(responseCode = "403", description = "Access forbidden - requires ADMIN role",
                           content = @Content),
                   @ApiResponse(responseCode = "429", description = "Too Many Requests", content = @Content),
                   @ApiResponse(responseCode = "500", description = "Internal server error during synchronization",
                           content = @Content)
               })
    @PostMapping("/sync")
    public ResponseEntity<String> syncRegionals() {
        try {
            return ResponseEntity.ok(regionalSyncService.syncRegionals());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error during regional synchronization: " + e.getMessage());
        }
    }
}
