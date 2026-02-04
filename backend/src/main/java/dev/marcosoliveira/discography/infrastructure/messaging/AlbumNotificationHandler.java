package dev.marcosoliveira.discography.infrastructure.messaging;

import dev.marcosoliveira.discography.api.dto.AlbumResponseDTO;
import dev.marcosoliveira.discography.api.mapper.AlbumMapper;
import dev.marcosoliveira.discography.domain.event.AlbumCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class AlbumNotificationHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final AlbumMapper albumMapper;

    public AlbumNotificationHandler(SimpMessagingTemplate messagingTemplate, AlbumMapper albumMapper) {
        this.messagingTemplate = messagingTemplate;
        this.albumMapper = albumMapper;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAlbumCreated(AlbumCreatedEvent event) {
        log.info("Broadcasting new album notification for album ID: {}", event.getAlbum().getId());

        AlbumResponseDTO album = albumMapper.toResponseDto(event.getAlbum());

        messagingTemplate.convertAndSend("/topic/albums", album);

        log.info("Successfully broadcasted album to /topic/albums: {}", album.getTitle());
    }

}
