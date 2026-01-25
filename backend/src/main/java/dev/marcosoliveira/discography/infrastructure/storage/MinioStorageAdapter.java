package dev.marcosoliveira.discography.infrastructure.storage;

import dev.marcosoliveira.discography.application.port.StoragePort;
import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioStorageAdapter implements StoragePort {

    private final MinioClient minioClient;

    public MinioStorageAdapter(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String upload(String bucket, String fileName, InputStream inputStream, String contentType) {
        try {
            if (bucketNotExists(bucket)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Upload Error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void delete(String bucket, String fileName) {
        try {
            validateBucket(bucket);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Delete Error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String getPresignedUrl(String bucket, String fileName) {
        try {
            validateBucket(bucket);

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(fileName)
                            .expiry(30, TimeUnit.MINUTES)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Get URL error: " + e.getLocalizedMessage(), e);
        }
    }

    private void validateBucket(String bucket) throws Exception {
        if (bucketNotExists(bucket))
            throw new RuntimeException(String.format("Bucket %s not found!", bucket));
    }

    private boolean bucketNotExists(String bucket) throws Exception {
        return !minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
    }
}
