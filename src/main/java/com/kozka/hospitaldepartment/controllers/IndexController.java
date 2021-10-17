package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.repositories.AssignmentRepository;
import com.kozka.hospitaldepartment.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
