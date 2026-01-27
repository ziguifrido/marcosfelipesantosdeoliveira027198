package dev.marcosoliveira.discography.application.port;

import java.io.InputStream;

public interface StoragePort {

    void upload(String bucket, String fileName, InputStream inputStream, String contentType);

    void delete(String bucket, String fileName);

    String getPresignedUrl(String bucket, String fileName);

}
