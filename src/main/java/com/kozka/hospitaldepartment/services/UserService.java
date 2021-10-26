package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Kozka Ivan
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    AssignmentService assgService;

    public List<User> getAllDoctors() {
        return userRepo.getUsersByUserRole(UserRole.DOCTOR);
    }

    public List<User> getAllActiveDoctors() {
        return userRepo
                .getUsersByUserRoleAndActiveIsTrue(UserRole.DOCTOR);
    }

    public List<User> getAllInactiveDoctors() {
        return userRepo
                .getUsersByUserRoleAndActiveIsFalse(UserRole.DOCTOR);
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

    public List<User> getAllPatients() {
        return userRepo.getUsersByUserRole(UserRole.PATIENT);
    }

    public List<User> getAllActivePatients() {
        return userRepo
                .getUsersByUserRoleAndActiveIsTrue(UserRole.PATIENT);
    }

    public List<User> getAllInactivePatients() {
        return userRepo
                .getUsersByUserRoleAndActiveIsFalse(UserRole.PATIENT);
    }

    public User getUserByEmail(String email) {
        return userRepo.getUserByEmail(email);
    }

    public void save(User user) {
        userRepo.save(user);
    }

    public User getUserById(Integer id) {
        return userRepo.getUserById(id);
    }

    public void deleteById(Integer id) {
        userRepo.deleteById(id);
    }

    public User getCurrentLoggedUser() {
        String email = getCurrentAuthEmail();
        return userRepo.getUserByEmail(email);
    }

    public List<User> getAllActiveDoctorsAndNurses() {
        return userRepo.getUsersByActiveTrue()
                .stream()
                .filter(u -> u.getUserRole() == UserRole.DOCTOR
                        || u.getUserRole() == UserRole.NURSE)
                .collect(Collectors.toList());
    }

    public List<Assignment> getHealthCardFor(User user) {
        return assgService.getUserMedCard(user.getId());
    }

    public Set<User> getAllPatientsForDoctor(User user) {
        return userRepo.getAllDoctorsPatients(user.getId());
    }

    public Set<User> getAllActivePatientsForDoctor(User user) {
        return userRepo.getAllDoctorsPatients(user.getId());
    }

    @Transactional
    public void update(User user) {
        userRepo.setUserDetailsById(
                user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPass(),
                user.getUserRole(), user.getSpecialization(),
                user.getActive(), user.getBirth(), user.getId()
        );
    }

    public void remove(Integer id) {
        userRepo.deleteById(id);
    }

    public void toArchive(Integer id) {
        var u = getUserById(id);
        u.setActive(false);

        userRepo.save(u);
    }

    public void toArchive(User u) {
        u.setActive(false);
        userRepo.save(u);
    }

    public void unArchive(Integer id) {
        var u = getUserById(id);
        u.setActive(true);

        userRepo.save(u);
    }

    public void unArchive(User u) {
        u.setActive(true);
        userRepo.save(u);
    }
}
