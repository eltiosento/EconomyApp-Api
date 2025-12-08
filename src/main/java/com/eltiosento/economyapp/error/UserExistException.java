package com.eltiosento.economyapp.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserExistException extends RuntimeException {

    private static final long serialVersionUID = 2L;

    public UserExistException(String message) {
        super(message);
    }

}
