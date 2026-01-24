package dev.marcosoliveira.discography.domain.model;

import dev.marcosoliveira.discography.domain.exception.DomainException;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@EqualsAndHashCode
@Builder
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageReference {
    private String bucket;
    private String objectKey;
    private String contentType;

    public ImageReference(String bucket, String objectKey, String contentType) {
        if (bucket == null || bucket.isBlank()) throw new DomainException("Bucket cannot be empty!");
        if (objectKey == null || objectKey.isBlank()) throw new DomainException("Object Key cannot be empty!");
        if (contentType == null || contentType.isBlank()) throw new DomainException("Content Type cannot be empty!");

        setBucket(bucket);
        setObjectKey(objectKey);
        setContentType(contentType);
    }

    public static ImageReference of(String bucket, String objectKey, String contentType) {
        return new ImageReference(bucket, objectKey, contentType);
    }
}
