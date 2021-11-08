package com.kozka.hospitaldepartment.exceptions;

/**
 * @author Kozka Ivan
 */
public class ApiException extends RuntimeException{
    public ApiException() {
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
