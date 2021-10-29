package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.AssignmentType;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.entities.UserRole;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        switch (order) {
            case "al":
                patients.sort(Comparator.comparing(User::getFullName).reversed());
                break;
            case "br":
                patients.sort(Comparator.comparing(User::getBirth));
                break;
        }

        Page<User> page = userService.getPageFor(patients, pageable);

        model.addAttribute("current_logged_in", current);
        model.addAttribute("page", page);
        model.addAttribute("order", "or=" + order);

        return "staff/my-patients";
    }

    @GetMapping("/health-card")
    public String getHealthCard(Model model) {
        var user = userService.getCurrentLoggedUser();
        var healthCard = userService.getHealthCardFor(user);

        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        return "patient/health-card";
    }
}
