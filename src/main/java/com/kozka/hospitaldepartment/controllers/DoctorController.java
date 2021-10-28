package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/doctors")
@AllArgsConstructor
public class DoctorController {

    private final UserService userService;

    @GetMapping
    public String getDoctors(Model model) {
        var doctors = userService.getAllActiveDoctorsAndNurses();
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("current_logged_in", current);
        model.addAttribute("doctors", doctors);
        return "patient/doctors";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addDoctor(
            Model model
    ) {
        var user = new User();
        user.setUserRole(UserRole.DOCTOR);
        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/users-add";
    }

    @GetMapping("/add-nurse")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addNurse(
            Model model
    ) {
        var user = new User();
        user.setUserRole(UserRole.NURSE);
        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/users-add";
    }
}
