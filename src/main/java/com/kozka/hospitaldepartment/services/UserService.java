package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<User> getAllNurses() {
        return getAllByRole(UserRole.NURSE);
    }

    public List<User> getAllPatients() {
        return getAllByRole(UserRole.PATIENT);
    }
}
