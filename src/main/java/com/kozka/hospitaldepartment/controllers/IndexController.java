package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import com.kozka.hospitaldepartment.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Kozka Ivan
 */
@Controller
public class IndexController {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AssignmentRepository assgRepo;

    @Autowired
    private AssignmentService assgService;

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/assgs")
    public String getAssgs(Model model) {
        List<Assignment> assgs = assgRepo.findAll();
        model.addAttribute("assgs", assgs);
        return "assgs";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable("id") Integer id, Model model) {
        var user = userRepo.getUserById(id);
        var medicalCard =
                assgService.getUserMedCard(id);

        model.addAttribute("user", user);
        model.addAttribute("medCard", medicalCard);
        return "user";
    }
}
