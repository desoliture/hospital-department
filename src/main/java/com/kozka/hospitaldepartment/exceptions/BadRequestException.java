package com.kozka.hospitaldepartment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Kozka Ivan
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ApiException{
    public BadRequestException() {
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
