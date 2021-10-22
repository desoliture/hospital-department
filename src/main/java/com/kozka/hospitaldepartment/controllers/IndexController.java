package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * @author Kozka Ivan
 */
@Controller
public class IndexController {
    @Autowired
    UserService userService;

    @Autowired
    AssignmentService assignmentService;

    @GetMapping
    public String getIndex() {
        return "redirect:/cabinet";
    }

    @GetMapping("/cabinet")
    public String getIndex(Model model) {
        var user = userService.getCurrentLoggedUser();
        model.addAttribute("current_logged_in", user);
        return "patient/cab";
    }

    @GetMapping("/assignments")
    public String getAssignments(Model model) {
        var user = userService.getCurrentLoggedUser();
        var assgs = assignmentService.getAllForPatient(user);
        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        model.addAttribute("current_logged_in", user);
        model.addAttribute("assignments", assgs);
        model.addAttribute("current_date", LocalDateTime.now());
        return "patient/assignments";
    }

    @GetMapping("/doctors")
    public String getDoctors(Model model) {
        var doctors = userService.getAllActiveDoctorsAndNurses();

        model.addAttribute("doctors", doctors);
        return "patient/doctors";
    }

    @GetMapping("/health-card")
    public String getHealthCard(Model model) {
        var user = userService.getCurrentLoggedUser();
        var healthCard = userService.getHealthCardFor(user);

//        model.addAttribute("current_logged_in", user);
        model.addAttribute("health_card", healthCard);
        return "patient/health-card";
    }

    @GetMapping("/user/{id}")
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

    @GetMapping("/manage")
    public String getManage(Model model) {
        var user = userService.getCurrentLoggedUser();
        model.addAttribute("managing", user);

        return "patient/manage";
    }

    @PostMapping("/manage")
    public String setManage(@ModelAttribute("managing") User user) {

        if (!userService.getUserById(user.getId())
                .getEmail().equals(user.getEmail()))
        {
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            SecurityContextHolder.clearContext();
        }

        userService.update(user);

        return "redirect:/";
    }
}
