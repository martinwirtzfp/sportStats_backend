package com.sports.backend.shared.v1.application.exception;

import com.sports.backend.shared.v1.application.exception.error.ApplicationError;
import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ApplicationError error;

    public ApplicationException(final ApplicationError error) {
        super(error.getMessage());
        this.error = error;
    }

    public ApplicationException(final ApplicationError error, final String detail) {
        super(error.getMessage() + ": " + detail);
        this.error = error;
    }
}
