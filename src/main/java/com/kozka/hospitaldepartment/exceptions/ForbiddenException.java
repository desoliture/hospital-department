package com.kozka.hospitaldepartment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Kozka Ivan
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends ApiException {

    public ForbiddenException() {
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
