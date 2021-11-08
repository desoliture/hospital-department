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
@RequestMapping("/admins")
@AllArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getAdmins(Model model) {
        var admins = userService.getAllActiveAdmins();

        model.addAttribute("admins", admins);
        model.addAttribute(
                "current_logged_in",
                userService.getCurrentLoggedUser()
        );
        return "admin/admins";
    }

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addAdmin(Model model) {
        var user = new User();
        user.setUserRole(UserRole.ADMIN);
        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/users-add";
    }

}
