package com.kozka.hospitaldepartment.exception_handlers;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * @author Kozka Ivan
 */
@ControllerAdvice
@Log4j2
public class GlobalControllerExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleItemNotFoundException(NoSuchElementException e) {
        log.error("No Such Element Exceptions happen! Incorrect ID. " + e.getMessage());
        return new ResponseEntity<>("No Such Element Exceptions happen! Incorrect Data. ", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    protected ResponseEntity<String> handleValidationException(BindException e) {
        log.error("Validation Exceptions happen! Incorrect data entry. " + e.getMessage());
        return new ResponseEntity<>("Validation Exceptions happen! Incorrect data entry.: ",
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> handleError(Exception ex) {
        log.error("Exceptions happen! " + ex.getMessage());
        return new ResponseEntity<>("Exceptions happen!: ", HttpStatus.BAD_REQUEST);
    }
}
