package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
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
@RequestMapping("/patients")
@AllArgsConstructor
public class PatientController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAllPatients(Model model) {
        var patients = userService.getAllActivePatients();
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("patients", patients);
        model.addAttribute("current_logged_in", current);

        return "admin/patients";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPatient(
            Model model
    ) {
        model.addAttribute("new_user", new User());
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());
        return "admin/patients-add";
    }
}
