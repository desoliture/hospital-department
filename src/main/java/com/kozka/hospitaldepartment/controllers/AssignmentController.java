package com.kozka.hospitaldepartment.controllers;

import com.kozka.hospitaldepartment.entities.Assignment;
import com.kozka.hospitaldepartment.entities.User;
import com.kozka.hospitaldepartment.services.AssignmentService;
import com.kozka.hospitaldepartment.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

/**
 * @author Kozka Ivan
 */
@Controller
@RequestMapping("/assgs")
public class AssignmentController {
    @Autowired
    AssignmentService assgsService;

    @Autowired
    UserService userService;

    @GetMapping
    public String getAssgs(Model model) {
        List<Assignment> assgs = assgsService.getAll();
        assgs.sort(Comparator.comparing(Assignment::getAssignmentDate));

        String email = userService.getCurrentAuthEmail();
        User current = userService.getUserByEmail(email);

        model.addAttribute("assgs", assgs);
        model.addAttribute("currentUser", current);
        return "assgs";
    }
}
