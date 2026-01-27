package dev.marcosoliveira.discography.application.service;

import dev.marcosoliveira.discography.application.port.StoragePort;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class StorageService {

    private static final String ARTIST_BUCKET = "artist-profile-image";
    private static final String ALBUM_BUCKET = "album-cover";
    private final StoragePort storagePort;

    public StorageService(StoragePort storagePort) {
        this.storagePort = storagePort;
    }

    public ImageReference uploadArtistProfileImage(UUID artistId, InputStream content, String contentType) {
        String filename = String.format("artist_%s_%d", artistId, System.currentTimeMillis());

        storagePort.upload(ARTIST_BUCKET, filename, content, contentType);

        return new ImageReference(ARTIST_BUCKET, filename, contentType);
    }

    public ImageReference uploadAlbumCover(UUID albumId, InputStream content, String contentType) {
        String filename = String.format("album_%s_%d", albumId, System.currentTimeMillis());

        storagePort.upload(ALBUM_BUCKET, filename, content, contentType);

        return new ImageReference(ALBUM_BUCKET, filename, contentType);
    }

    public void delete(ImageReference imageReference)  {
        if (imageReference != null)
            storagePort.delete(imageReference.getBucket(), imageReference.getObjectKey());
    }

    public String getUrl(ImageReference imageReference) {
        if (imageReference == null)
            return null;

        return storagePort.getPresignedUrl(imageReference.getBucket(), imageReference.getObjectKey());
    }
}
