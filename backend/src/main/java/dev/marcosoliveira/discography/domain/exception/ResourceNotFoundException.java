package dev.marcosoliveira.discography.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s with ID %s not found!", resourceName, id));
    }
}
