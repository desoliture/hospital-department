package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Kozka Ivan
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    private List<User> getAllByRole(UserRole role) {
        return userRepository.getUsersByUserRole(role);
    }

    public List<User> getAllDoctors() {
        return getAllByRole(UserRole.DOCTOR);
    }

    public String getCurrentAuthEmail() {
        String email;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            email = ((UserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }

        return email;
    }

    public List<User> getAllNurses() {
        return getAllByRole(UserRole.NURSE);
    }

    public List<User> getAllPatients() {
        return getAllByRole(UserRole.PATIENT);
    }
}
