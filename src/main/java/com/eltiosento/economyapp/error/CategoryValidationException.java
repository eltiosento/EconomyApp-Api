package com.eltiosento.economyapp.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoryValidationException extends RuntimeException {
    private static final long serialVersionUID = 2L;

    public CategoryValidationException(String message) {
        super(message);
    }
}
