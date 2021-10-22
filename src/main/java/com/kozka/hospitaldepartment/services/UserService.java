package com.kozka.hospitaldepartment.services;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.InactiveCause;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.repositories.CauseRepository;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    @Autowired
    CauseRepository causeRepo;

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

    public void makeInactive(User user) {
        InactiveCause cause = new InactiveCause(
                user,
                LocalDateTime.now(),
                "deleted by admin"
        );
        user.setActive(false);

        causeRepo.save(cause);
        userRepo.save(user);
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

    public void ifNotAppointedDeleteElseMakeInactive(Integer id) {
        User user = getUserById(id);

        List<Assignment> assgs = assgService.getAll();

        boolean appointed = assgs.stream()
                .anyMatch( a ->
                                a.getPatient() != null
                                        && a.getPatient().equals(user)
                                ||
                                a.getAssigned() != null
                                        && a.getAssigned().equals(user)
                                ||
                                a.getAssigner() != null
                                        && a.getAssigner().equals(user)
                );

        if (!appointed) {
            deleteById(id);
        }
        else {
            makeInactive(user);
        }
    }

    public void makeActive(Integer id) {
        makeActive(userRepo.getUserById(id));
    }

    public void makeActive(User user) {
        user.setActive(true);
        userRepo.save(user);
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
}
