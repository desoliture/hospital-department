package com.kozka.hospitaldepartment.entities;

import java.util.Locale;

/**
 * @author Kozka Ivan
 */
public enum Specialization {
    ALLERGIST, ANESTHETIST, REANIMATOLOGIST,
    EPIDEMIOLOGIST, CARDIOLOGIST, ONCOLOGIST,
    TRAUMATOLOGIST, ORTHOPEDIST, PHYSIOTHERAPIST,
    RADIOLOGIST, TOXICOLOGIST, PULMONOLOG, UROLOGIST,
    THERAPIST, PEDIATRICIAN, INFECTIONIST, SURGEON,
    GYNECOLOGIST, NEUROPATHOLOGIST, PSYCHIATRIST,
    NARCOLOGIST, OPHTHALMOLOGIST, OTOLARYNGOLOGIST,
    DERMATOVENEROLOGIST, DENTIST, FAMILY_DOC;

    public String namePar() {
        if (this == FAMILY_DOC) return "Family doctor";

        StringBuilder sb =
                new StringBuilder(
                        name().toLowerCase()
                );

        return sb.substring(0, 1)
                + sb.substring(1).toLowerCase();
    }
}
