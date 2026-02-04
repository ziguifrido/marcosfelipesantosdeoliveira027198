package dev.marcosoliveira.discography.domain.event;

import dev.marcosoliveira.discography.domain.model.Album;
import lombok.Getter;

import java.io.Serial;
import java.util.EventObject;

@Getter
public class AlbumCreatedEvent extends EventObject {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Album album;

    public AlbumCreatedEvent(Object source, Album album) {
        super(source);
        this.album = album;
    }

}
