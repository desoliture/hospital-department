package com.kozka.hospitaldepartment.utils;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Kozka Ivan
 */
@Service
public class ControllerUtil {
    public static Map<String, String> getErrorsMap(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        FieldError::getDefaultMessage
                ));
    }
}
