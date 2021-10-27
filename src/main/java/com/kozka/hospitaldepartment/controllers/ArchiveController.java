package com.kozka.hospitaldepartment.controllers;

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
@RequestMapping("/archive")
@AllArgsConstructor
public class ArchiveController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchive(Model model) {
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/archive";
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchivedPatients(Model model) {
        var users = userService.getAllInactivePatients();

        model.addAttribute("archived", users);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());

        return "admin/archived-users";
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getArchivedDoctors(Model model) {
        var users = userService.getAllInactiveDoctors();

        model.addAttribute("archived", users);
        model.addAttribute("current_logged_in", userService.getCurrentLoggedUser());

        return "admin/archived-users";
    }
}
