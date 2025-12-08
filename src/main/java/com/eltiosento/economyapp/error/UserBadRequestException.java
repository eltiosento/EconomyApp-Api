package com.eltiosento.economyapp.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserBadRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserBadRequestException(String message) {
        super(message);
    }

}
