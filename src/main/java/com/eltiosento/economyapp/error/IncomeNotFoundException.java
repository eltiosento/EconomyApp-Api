package com.eltiosento.economyapp.error;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IncomeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IncomeNotFoundException(String message) {
        super(message);
    }

    public IncomeNotFoundException() {
        super("No incomes found.");
    }

}
