package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
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

    private final UserRepository userRepo;
    private final AssignmentRepository assgRepo;
    private final AssignmentService assgService;
    private final UserService userService;

    @Autowired
    public IndexController(UserRepository userRepo,
                           AssignmentRepository assgRepo,
                           AssignmentService assgService,
                           UserService userService) {
        this.userRepo = userRepo;
        this.assgRepo = assgRepo;
        this.assgService = assgService;
        this.userService = userService;
    }

    @GetMapping("/pa")
    public String getPa() {
        return "pa";
    }

    @GetMapping("/pats")
    public String getPats(Model model) {
        List<User> patients = userService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/docs")
    public String getDocs(Model model) {
        List<User> doctors = userService.getAllDoctors();
        model.addAttribute("doctors", doctors);
        return "doctors";
    }


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
