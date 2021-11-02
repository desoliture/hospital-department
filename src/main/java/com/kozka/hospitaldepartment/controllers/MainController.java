package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.UserService;
import com.kozka.hospitaldepartment.utils.ControllersUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kozka Ivan
 */
@Controller
@AllArgsConstructor
public class MainController {

    private final UserService userService;

    @GetMapping
    public String getIndex() {
        return "redirect:/cabinet";
    }

    @GetMapping("/cabinet")
    public String getIndex(Model model) {
        var user = userService.getCurrentLoggedUser();

        if (user.getUserRole() == UserRole.NURSE || user.getUserRole() == UserRole.DOCTOR) {
            var numOfPats = Map.of(user.getId(), userService.getAllActivePatientsForDoctor(user).size());
            model.addAttribute("num_of_pats_map", numOfPats);
        }

        model.addAttribute("current_logged_in", user);
        return "patient/cab";
    }

    @GetMapping("/my-patients")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'NURSE')")
    public String getMyPatients(
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

        var current = userService.getCurrentLoggedUser();
        List<User> patients = new ArrayList<>(userService.getAllActivePatientsForDoctor(current));

        ControllersUtil.sortingByOrder(order, patients);

        Page<User> page = userService.getPageFor(patients, pageable);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("page", page);
        model.addAttribute("order", "or=" + order);

        return "staff/my-patients";
    }

    @GetMapping("/health-card")
    @PreAuthorize("hasAuthority('PATIENT')")
    public String getHealthCard(Model model) {
        var user = userService.getCurrentLoggedUser();

        var healthCard = userService.getHealthCardFor(user);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        return "patient/health-card";
    }
}
