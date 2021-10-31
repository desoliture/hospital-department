package com.kozka.hospitaldepartment.entities;

/**
 * @author Kozka Ivan
 */
public enum AssignmentType {
    PROCEDURE, MEDICINE, OPERATION, VISIT;

    public String toStringFirstUpper() {
        StringBuilder sb =
                new StringBuilder(
                        name().toLowerCase()
                );

        return sb.substring(0, 1)
                + sb.substring(1).toLowerCase();
    }
}
