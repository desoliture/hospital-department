package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.exceptions.BadRequestException;
import com.kozka.hospitaldepartment.exceptions.ForbiddenException;
import com.kozka.hospitaldepartment.exceptions.InvalidRequestSourceException;
import com.kozka.hospitaldepartment.exceptions.UserNotFoundException;
import com.kozka.hospitaldepartment.services.UserService;
import com.kozka.hospitaldepartment.utils.ControllersUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
            @PathVariable("id") User user,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        if (current.getId().equals(user.getId()))
            return "redirect:/";


        if (current.getUserRole() != UserRole.ADMIN) {
            if (!(
                    (current.getUserRole() == UserRole.DOCTOR
                            || current.getUserRole() == UserRole.NURSE
                    ) && userService
                            .isDoctorAssignedToPatient(current, user))
            ) {
                if (!userService.isDoctorAssignedToPatient(user, current))
                    throw new ForbiddenException("You have no access!");
            }
        }


        Map<Integer, Integer> numOfPatsMap = numOfPatsMap = userService.getAllActiveDoctorsAndNurses()
                .stream().collect(
                        Collectors.toMap(
                                User::getId,
                                d -> userService.getAllActivePatientsForDoctor(d).size()
                        )
                );

        model.addAttribute("explored_user", user);
        model.addAttribute("current_logged_in", current);
        model.addAttribute("num_of_pats_map", numOfPatsMap);

        return "patient/explore";
    }

    @GetMapping("{id}/edit")
    public String getEdit(
            @PathVariable("id") User user,
            Model model
    ) {
        var current = userService.getCurrentLoggedUser();

        String curRole = current.getUserRole().name();
        String usRole = user.getUserRole().name();

        boolean selfEditing = current.getId().equals(user.getId());

        if (selfEditing || curRole.equals("ADMIN")) {
            model.addAttribute("current_logged_in", current);
            model.addAttribute("editing", user);
            return "patient/edit";
        } else throw new ForbiddenException("You have no access!");
    }

    @PostMapping("/edit")
    public String setManage(
            @ModelAttribute("editing") User user
    ) {
        var current = userService.getCurrentLoggedUser();
        var edited = userService.getUserById(user.getId());


        if (!(current.getUserRole().name().equals("ADMIN")
                || current.getId().equals(edited.getId())
        )) throw new ForbiddenException("You have no access!");


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

    @GetMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addUser(
            Model model
    ) {
        var user = new User();

        model.addAttribute("new_user", user);
        model.addAttribute("current_logged_in",
                userService.getCurrentLoggedUser());

        return "admin/users-add-role";
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String addUser(
            @Valid @ModelAttribute("new_user") User user,
            BindingResult bindingResult,
            Model model,
            HttpServletRequest request
    ) {
        var errors = ControllersUtil.getErrorsMap(bindingResult);

        if (bindingResult.hasFieldErrors("userRole")) {
            String prevUrl = request.getHeader("Referer");
            String msg = "Invalid request source!";

            throw new InvalidRequestSourceException(msg, prevUrl, request);
        }

        if (user.getUserRole() == UserRole.PATIENT
                && user.getBirth() == null)
            bindingResult.addError(
                    new FieldError(
                            "new_user", "birth",
                            "You must enter the birth date"
                    )
            );

        if (userService.isExist(user.getEmail())) {
            errors.put("email", "Email already taken!");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("new_user", user);
            model.addAttribute("current_logged_in",
                    userService.getCurrentLoggedUser());

            model.addAttribute("errors_map", errors);

            return "admin/users-add";
        }

        user.setActive(true);
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
        String referer = request
                .getHeader("Referer")
                .replace("http://localhost:8080", "");

        if (userService.getCurrentLoggedUser()
                .getId()
                .equals(user.getId()))
            throw new BadRequestException("Can't remove current logged in user!");

        if (user.getUserRole() == UserRole.NURSE || user.getUserRole() == UserRole.DOCTOR) {
            var numOfPats = Map.of(user.getId(), userService.getAllActivePatientsForDoctor(user).size());
            model.addAttribute("num_of_pats_map", numOfPats);
        }

        model.addAttribute("user_to_remove", user);
        model.addAttribute("previous_url", referer);

        return "admin/users-remove";
    }

    @PostMapping("/remove")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(
            @ModelAttribute("id") Integer id,
            @ModelAttribute("previous_url") String previousUrl
    ) {
        if (userService
                .getCurrentLoggedUser()
                .getId().equals(id)
        ) throw new BadRequestException("Can't remove current logged in user!");

        userService.toArchive(id);

        return "redirect:" + previousUrl;
    }

    @GetMapping("/{id}/health-card")
    public String getHealthCard(
            Model model,
            @PathVariable("id") User patient
    ) {
        var current = userService.getCurrentLoggedUser();

        if (current.getUserRole() == UserRole.PATIENT
                && current.getId().equals(patient.getId()))
            return "redirect:/health-card";

        if (!userService.isDoctorAssignedToPatient(current, patient)
                && !current.getUserRole().name().equals("ADMIN")
        ) throw new ForbiddenException("You have no access!");

        var healthCard = userService.getHealthCardFor(patient);

        model.addAttribute("current_logged_in", current);
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
