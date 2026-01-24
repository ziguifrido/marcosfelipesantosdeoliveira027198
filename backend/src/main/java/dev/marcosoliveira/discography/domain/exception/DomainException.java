package dev.marcosoliveira.discography.domain.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
