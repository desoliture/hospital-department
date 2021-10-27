package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String getUser(
            @PathVariable Integer id,
            Model model)
    {
        var user = userService.getUserById(id);
        var current = userService.getCurrentLoggedUser();

        if (user.equals(current)) return "redirect:/";

        model.addAttribute("explored_user", user);
        model.addAttribute("current_logged_in", current);

        return "patient/explore";
    }

    @GetMapping("{id}/edit")
    public String getManage(
            @PathVariable("id") User user,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        model.addAttribute("current_logged_in", current);
        model.addAttribute("editing", user);
        return "patient/edit";
    }

    @PostMapping("/edit")
    public String setManage(@ModelAttribute("editing") User user) {

        var current = userService.getCurrentLoggedUser();
        var edited = userService.getUserById(user.getId());

        if (user.getPass() == null || user.getPass().equals(""))
            user.setPass(edited.getPass());

        if (current.getId().equals(edited.getId())) {

            if (!edited.getEmail().equals(user.getEmail())) {
                SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
                SecurityContextHolder.clearContext();
            }
        }

        user.setPass(
                userService.encodePass(
                        user.getPass()
                )
        );

        userService.update(user);

        return "redirect:/";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addUser(
            @ModelAttribute("new_user") User user,
            @ModelAttribute("user_role") UserRole userRole
    ) {
        user.setActive(true);
        user.setUserRole(userRole);
        user.setPass(
                userService.encodePass(
                        user.getPass()
                )
        );

        userService.save(user);

        return "redirect:/";
    }

    @GetMapping("/{id}/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(
            @PathVariable("id") User user,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        if (userService.getCurrentLoggedUser()
                .getId()
                .equals(user.getId()))
            return "redirect:" + previousUrl;

        model.addAttribute("user_to_remove", user);
        model.addAttribute("previous_url", previousUrl);

        return "admin/users-remove";
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        userService.toArchive(id);

        return "redirect:" + previousUrl;
    }

    @GetMapping("/{id}/health-card")
    public String getHealthCard(
            Model model,
            @PathVariable("id") User patient
    ) {
        var user = userService.getCurrentLoggedUser();
        var healthCard = userService.getHealthCardFor(patient);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        model.addAttribute("patient", patient);
        return "patient/health-card";
    }

    @GetMapping("/{id}/unarchive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String unarchiveUser(
            @PathVariable("id") User user,
            Model model,
            HttpServletRequest request
    ) {
        String previousUrl = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        if (userService.getCurrentLoggedUser()
                .getId()
                .equals(user.getId()))
            return "redirect:" + previousUrl;

        model.addAttribute("user_to_unarchive", user);
        model.addAttribute("previous_url", previousUrl);

        return "admin/users-unarchive";
    }

    @PostMapping("/unarchive")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String unarchiveUser(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        userService.unArchive(id);

        return "redirect:" + previousUrl;
    }
}
