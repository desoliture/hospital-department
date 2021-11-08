package com.kozka.hospitaldepartment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kozka Ivan
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestSourceException extends ApiException {
    public InvalidRequestSourceException(
            String msg,
            String refererUrl,
            HttpServletRequest request
    ) {
        //TODO
    }
}
