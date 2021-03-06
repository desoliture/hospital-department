package com.kozka.hospitaldepartment.exception_handlers;

import com.kozka.hospitaldepartment.exceptions.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Kozka Ivan
 */
@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerExceptionHandler {
    private final Logger logger;

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleError(Exception ex, HttpStatus status) {
        logger.error("Exceptions happen! " + ex.getMessage());
        return String.valueOf(status.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleErrorNotApi(Exception ex, HttpStatus status) {
        logger.error("Not api exceptions happen! " + ex.getMessage());
        return String.valueOf(status.value());
    }
}
