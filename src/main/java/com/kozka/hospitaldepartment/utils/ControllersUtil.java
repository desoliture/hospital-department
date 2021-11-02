package com.kozka.hospitaldepartment.utils;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
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

    public static List<String[]> healthCardToString(User patient, List<Assignment> healthCard) {
        return healthCard.stream()
                .map(a -> {
                    String date = a.getAssignmentDate().format(
                            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
                    );

                    String asType = a.getAssgType()
                            .toStringFirstUpper() + ": " + a.getDescription();

                    String assigner = "Appointed by: "
                            + a.getAssigner().getFirstName()
                            + " "
                            + a.getAssigner().getLastName()
                            + (a.getAssigner().getSpecialization() != null ?
                            (" (" + a.getAssigner().getSpecialization().namePar() + ")"):"");
                    String assigned = "Held by: "
                            + a.getAssigned().getFirstName()
                            + " "
                            + a.getAssigned().getLastName()
                            + (a.getAssigned().getSpecialization() != null ? (
                            " (" + a.getAssigned().getSpecialization().namePar() + ")"):"");

                    String conclusion = "Conclusion: " + a.getConclusion();

                    return new String[] {date, asType, assigner, assigned, conclusion};
                }).collect(Collectors.toList());
    }

    public static void sortingByOrder(String order, List<User> patients) {
        switch (order) {
            case "al":
                patients.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "br":
                patients.sort(Comparator.comparing(User::getBirth));
                break;
        }
    }
}
