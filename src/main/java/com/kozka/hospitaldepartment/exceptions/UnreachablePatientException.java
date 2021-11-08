package com.kozka.hospitaldepartment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Kozka Ivan
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnreachablePatientException extends ApiException{
    public UnreachablePatientException() {
        super();
    }

    public UnreachablePatientException(String message) {
        super(message);
    }
}
