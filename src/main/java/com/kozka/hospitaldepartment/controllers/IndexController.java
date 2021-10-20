package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AssignmentRepository assgRepo;

    @Autowired
    private AssignmentService assgService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getIndex() {
        return "redirect:/cab";
    }

    @GetMapping("/cab")
    public String getCab(Model model) {
        String email = userService.getCurrentAuthEmail();
        User current = userRepo.getUserByEmail(email);

        model.addAttribute("currentUser", current);
        return "cab";
    }

    @GetMapping("/pats")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String getPats(Model model) {
        List<User> patients = userService.getAllPatients();
        patients.sort(Comparator.comparing(User::getId));

        String email = userService.getCurrentAuthEmail();
        User current = userRepo.getUserByEmail(email);

        model.addAttribute("patients", patients);
        model.addAttribute("currentUser", current);
        return "patients";
    }

    @GetMapping("/docs")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public String getDocs(Model model) {
        List<User> doctors = userService.getAllDoctors();
        doctors.sort(Comparator.comparing(User::getId));

        model.addAttribute("doctors", doctors);
        return "doctors";
    }

    @GetMapping("/pats/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPat(Model model) {
        model.addAttribute("new_user", new User());
        return "add_pat";
    }

    @PostMapping("/pats/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPatPost(@ModelAttribute("new_user") User user) {
        user.setActive(true);
        user.setUserRole(UserRole.PATIENT);

        userRepo.save(user);

        return "redirect:/pats";
    }

    @GetMapping("/pats/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String modPat(
            @PathVariable("id") Integer id,
            Model model)
    {
        User user = userRepo.getUserById(id);
        model.addAttribute("modify_user", user);
        return "mod_pat";
    }

    @PostMapping("/pats/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String modPatPost(@ModelAttribute("modify_user") User user) {

        userRepo.save(user);

        return "redirect:/pats";
    }

    @GetMapping("/pats/rem/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String remPat(
            @PathVariable("id") Integer id,
            Model model)
    {
        userRepo.deleteById(id);

        return "redirect:/pats";
    }
}
