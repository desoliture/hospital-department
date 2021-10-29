package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;

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
    public String getAllPatients(
            @RequestParam(
                    value = "or",
                    required = false,
                    defaultValue = ""
            ) String order,
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable,
            Model model
    ) {
        var patients = userService.getAllActivePatients();
        var current = userService.getCurrentLoggedUser();

        switch (order) {
            case "al":
                patients.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "br":
                patients.sort(Comparator.comparing(User::getBirth));
                break;
        }

        Page<User> page = userService.getPageFor(patients, pageable);

        model.addAttribute("page", page);
        model.addAttribute("current_logged_in", current);
        model.addAttribute("order", "or=" + order);

        return "admin/patients";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addPatient(
            Model model
    ) {
        var user = new User();
        user.setUserRole(UserRole.PATIENT);
        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());
        return "admin/users-add";
    }
}
