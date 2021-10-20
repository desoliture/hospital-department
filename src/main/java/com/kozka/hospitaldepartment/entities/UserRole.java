package com.kozka.hospitaldepartment.entities;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Kozka Ivan
 */
public enum UserRole implements GrantedAuthority {
    ADMIN, PATIENT, DOCTOR, NURSE;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
