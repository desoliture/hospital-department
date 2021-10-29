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
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/doctors")
@AllArgsConstructor
public class DoctorController {

    private final UserService userService;

    @GetMapping
    public String getDoctors(
            Model model,
            @RequestParam(
                    value = "or",
                    required = false,
                    defaultValue = ""
            ) String order,
            @RequestParam(
                    value = "sts",
                    required = false,
                    defaultValue = ""
            ) String stuffToShow,
            @PageableDefault(
                    sort = {"assignmentDate"},
                    direction = Sort.Direction.DESC,
                    size = 1
            ) Pageable pageable
    ) {
        List<User> doctors;
        var current = userService.getCurrentLoggedUser();

        switch (stuffToShow) {
            case "doc":
                doctors = userService.getAllActiveDoctors();
                break;
            case "nur":
                doctors = userService.getAllActiveNurses();
                break;
            default:
                doctors = userService.getAllActiveDoctorsAndNurses();
                break;
        }

        var numOfPatsMap = doctors.stream().collect(
                Collectors.toMap(
                        User::getId,
                        d -> userService.getAllActivePatientsForDoctor(d).size()
                )
        );

        switch (order) {
            case "al":
                doctors.sort(Comparator.comparing(User::getFullName));
                break;
            case "spec":
                doctors.removeIf(u -> u.getUserRole() == UserRole.NURSE);
                doctors.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "nop":
                doctors.sort((d1, d2) ->
                    numOfPatsMap.get(d2.getId()).compareTo(numOfPatsMap.get(d1.getId()))
                );
                break;
        }

        Page<User> page = userService.getPageFor(doctors, pageable);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("page", page);
        model.addAttribute("num_of_pats_map", numOfPatsMap);
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
